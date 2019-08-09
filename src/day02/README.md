## CAS是什么？
&#8194;&#8194;&#8194;&#8194;CAS的全称是Compare-And-Swap,**它是一条CPU并发原语**。
它的功能是判断内存某个位置的值是否为预期值，如果是则更改为新的值，这个过程是原子的。  
&#8194;&#8194;&#8194;&#8194;CAS并发原语体现在Java语言中就是sun.misc.Unsafe类中的各个方法。调用Unsafe类中的CAS方法，
JVM会帮我们实现出CAS汇编指令。这是一种完全依赖于**硬件**的功能，通过它实现了原子操作。再次强调，由于CAS是一种系统原语，
原语属于操作系统用语范畴，是由若干条指令组成的，用语完成某个功能的一个过程，并且原语的执行必须是连续的，
在执行过程中不允许被中断，也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。

- 比较并交换
```java
/**
 * 1.CAS是什么？
 *      比较并交换(compareAndSet)
 * @author chenxiaonuo
 * @date 2019-08-09 14:01
 */
public class CASDemo {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        System.out.println(atomicInteger.compareAndSet(5, 2019) + " current data: " + atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(5, 2014) + " current data: " + atomicInteger.get());
    }

}
```
- CAS底层原理

```java
public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;

    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;//内存地址偏移量

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int value;
    
    public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }
}
``` 
```
java
    //var1 AtomicInteger对象本身
    //var2 该对象值的引用地址
    //var4 需要变动的值
    //var5 通过var1和var2找出的主存中真实的值
    //用该对象当前的值与var5比较，如果相同，更新var5+var4并返回true；如果不同，继续取值后再比较，直到更新完成
    public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));

        return var5;
    }
```
假设线程A和线程B两个线程同时执行getAndAddInt操作(分别跑在不同CPU上):   
1) AtomicInteger里面的value原始值为3，即主内存中AtomicInteger的value为3，根据JMM模型，线程A和B各自持有一份值为3的value的副本分别到各自的工作内存
2) 线程A通过getIntVolatile(var1,var2)拿到value值3，这是线程A被挂起。
3) 线程B也通过getIntVolatile(var1,var2)获取到value值3，此时刚好线程B没有被挂起并执行compareAndSwapInt方法比较内存值也为3，
成功修改内存值为4，线程B完成。
4) 这时线程A回复，执行compareAndSwapInt方法比较，发现自己手里的值3与主内存中的值4不一致，说明该值已经被其他线程抢先一步修改过了，
那A线程本次修改失败，只能重新读取重新再来一遍。
5) 线程A重新获取value值，因为value被volatile修饰，所以其他线程对它的修改，线程A总是能够看到，线程A继续执行compareAndSwapInt进行比较替换，知道成功。

原理：  
1. Unsafe是CAS的核心类，由于Java方法无法直接访问底层系统，需要通过本地(native)方法来访问，Unsafe相当于一个后门，
基于该类可以直接操作特定内存的数据。Unsafe类存在于sun.misc包中，其内部方法操作可以像C的指针一样直接操作内存，
因为Java中CAS操作的执行一览与Unsafe类的方法。  
注意Unsafe类中的所有方法都是native修饰的，也就是说Unsafe类中的方法都可以调用操作系统底层资源执行相应任务
2. 变量valueOffset，表示该变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址来获取数据的。
3. 变量value用volatile，保证了多线程之间的内存可见性。

- CAS缺点
1. 循环时间长开销很大(如果CAS失败，会一直尝试，长时间不成功，可能会给CPU带来很大的开销)
2. 只能保证一个共享变量的原子操作
3. 引出来ABA问题