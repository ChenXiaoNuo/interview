## Volatile是什么？
volatile 是 Java 虚拟机提供的轻量级的同步机制，拥有以下三大特性：
- 保证可见性
- 不保证原子性
- 禁止指令重排

#### JMM(Java内存模型)
&#8194;&#8194;&#8194;&#8194;JMM(Java内存模型Java Memory Model，简称JMM)本身是一种抽象的概念并不真实存在，
它描述的是一组规则或规范，通过这组规范定义了程序中各个变量(包括实例字段，静态字段和构成素组对象的元素)的访问方式。  

&#8194;&#8194;JMM关于同步的规定：
1. 线程解锁前，必须把共享变量的值刷新回只内存
2. 线程加锁前，必须读取主内存的最新值到自己的工作内存
3. 加锁解锁是同一把锁

&#8194;&#8194;&#8194;&#8194;由于JVM运行程序实体是线程，而每个线程创建时JVM都会为其创建一个工作内存(有些地方称为栈空间)，工作内存是每个线程的私有数据区域，
而Java内存模型中规定所有变量都存储在主内存，主内存是共享内存区域，所有线程都可以访问，但线程对变量的操作(读取赋值等)必须在工作内存中进行，首先要将变量从主内存拷贝到自己的工作内存空间，
然后对变量进行操作，操作完成后再将变量写回主内存，不能直接操作主内存中的变量，各个线程中的工作内存中存储着主内存中的变量副本拷贝，
因此不同的线程间无法访问对方的工作内存，线程间的通信(传值)必须通过主内存来完成，其简要访问过程如下图：  
![JMM](JMM.png#pic_center)
- 可见性

&#8194;&#8194;&#8194;&#8194;各个线程对主内存中共享变量的操作都是各个线程各自拷贝到自己的工作内存进行操作然后再写回到主内存中的。  
&#8194;&#8194;&#8194;&#8194;这就可能存在一个线程 t1 修改了共享变量 X 的值但还未写回主内存时，另外一个线程 t2 又对主内存中同一个共享
变量 X 进行操作，但此时 t1 线程工作内存中共享变量 X 对线程 t2 来说并不可见，这种工作内存与主内存同步延迟现象就造成了可见性问题。
```java
/**
 * 验证volatile的可见性
 *  1. 例如 int number = 0; number变量之前根本没有添加volatile关键字,没有可见性
 *  2. 例如 int number = 0; number变量之前添加volatile关键字,有可见性
 * @author chenxiaonuo
 * @date 2019-08-06 17:15
 */
public class Demo01Volatile {

    public static void main(String[] args) {
        MyData myData = new MyData();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\tcome in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myData.addT060();
            System.out.println(Thread.currentThread().getName() + "\tupdate number value：" + myData.number);
        }, "t1").start();

        //第二个线程就是main线程
        //1. number变量之前根本没有添加volatile关键字，t1线程已经完成了number的修改，但是main线程没有接收到number改变了，因此一直等待
        //2. number变量之前添加volatile关键字，t1线程已经完成了number的修改，main线程接收到number改变了，满足条件继续执行
        while (myData.number == 0){
            //main线程一直在这里等待循环，直到number值不再等于0.
        }

        System.out.println(Thread.currentThread().getName() + "\tmission is over，main get number value：" + myData.number);
    }

}

class MyData{
        volatile int number = 0;
    
        public void addT060(){
            this.number = 60;
        }
}

```
- 原子性
```java
/**
 * 验证volatile不保证原子性
 *  1.原子性指的是什么意思？
 *      不可分割，完整性，也即某个线程正在做某个业务时，中间不可以被加塞或者被分割。
 *      需要整体完整，要么同时成功，要么同时失败
 *  2.volatile不保证原子性的案例演示
 *  3.如何解决
 *      * 加sync
 *      * 利用Atomic
 * @author chenxiaonuo
 * @date 2019-08-06 17:15
 */
public class Demo02Volatile {

    public static void main(String[] args) {
        MyData myData = new MyData();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myData.addPlusPlus();
                    myData.addAtomic();
                }
            }, String.valueOf(i)).start();
        }

        //需要等待上面的20个线程全部计算完成后，再用main线程取得最终的结果值
        while (Thread.activeCount() > 2){//main和gc线程，所以此处为2
            Thread.yield();
        }

        //出现了丢失写值的情况，写覆盖
        System.out.println(Thread.currentThread().getName() + " int type, finally number value：" + myData.number);
        //结果正确
        System.out.println(Thread.currentThread().getName() + " AtomicInteger type, finally number value：" + myData.atomicInteger);

    }
}

class MyData{
    volatile int number = 0;

    public void addPlusPlus(){
        //number++在多线程下是非线程安全的
        number++;
    }

    AtomicInteger atomicInteger = new AtomicInteger();
    public void addAtomic(){
        atomicInteger.getAndIncrement();
    }
}
```
- 有序性

计算机在执行程序时，为了提高性能，编译器和处理器常常会对指令做重排，一般分为以下三种
![指令重排](cookbook.png#pic_center)

&#8194;&#8194;&#8194;&#8194;单线程环境里面确保程序最终执行结果和代码顺序执行的结果一致。  
&#8194;&#8194;&#8194;&#8194;处理器在进行重排序时必须要考虑指令之间的数据依赖性。  
&#8194;&#8194;&#8194;&#8194;多线程环境中线程交替执行，由于编译器优化重排的存在，两个线程中使用的变量是否能够保证一致性是无法确定的，结果无法预测。  
&#8194;&#8194;&#8194;&#8194;volatile实现**禁止指令重排优化**，从而避免多线程环境下程序出现乱序执行的现象。
```java
public class ReSortSeq{
    
    //案例1
    //指令不重排：1234，指令重排可能执行的顺序：2134、1324
    public void mySort(){
            int x = 11;//语句1
            int y = 12;//语句2
            x = x + 5;//语句3
            y = x * x;//语句4
    }
    
    //案例2
    //多线程环境下，语句1与语句2不存在数据依赖性，可能语句2先于语句1执行，那么a的值可能等于5
    int a = 0;
    boolean flag =false;
    
    public void m1(){
        a = 1;          //语句1
        flag = true;    //语句2
    }
    
    public void m2(){
        if (flag){
            a = a + 5;  //语句3
            System.out.println("........returnValue: " + a);
        }
    }
    
}

```
#### 你在哪些地方用到过volatile
```java
/**
 * 单例模型DCL代码
 * @author chenxiaonuo
 * @date 2019-08-09 11:09
 */
public class SingletonDemo {

    private static volatile SingletonDemo instance = null;

    private SingletonDemo(){
        System.out.println(Thread.currentThread().getName() + " 我是构造方法SingletonDemo()");
    }

    /*
        DCL(Double Check Lock 双端检锁机制)
        DCL机制不一定线程安全，原因是有指令重排序的存在，加入volatile可以禁止指令重排
        某一个线程执行第一次检测，读取到的instance不为null时，instance的引用对可能没有完成初始化。
        instance = new SingletonDemo();可以分为以下三步：
            memory = allocate(); //1.分配对象内容空间
            instance(memory);    //2.初始化对象
            instance = memory;   //3.设置instance指向刚分配的内存地址，此时 instance != null
        步骤2与3不存在数据依赖关系，而且无论重排前还是重排后程序的执行结果在单线程中并没有改变，因此这种重排优化是允许的。
            memory = allocate(); //1.分配对象内容空间
            instance = memory;   //3.设置instance指向刚分配的内存地址，此时 instance != null，但是对象还没有初始化完成！
            instance(memory);    //2.初始化对象
        指令重排只会保证串行语义的执行的一致性(单线程)，但并不会关心多线程间的语义的一致性。
        所以当一条线程访问instance不为null时，由于instance实例未必已初始化完成，也就造成了线程安全问题
    */
    public static SingletonDemo getInstance(){
        if (instance == null){
            synchronized (SingletonDemo.class){
                if (instance == null){
                    instance = new SingletonDemo();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {

        //单线程
//        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
//        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
//        System.out.println(SingletonDemo.getInstance() == SingletonDemo.getInstance());
//

        //多线程
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                SingletonDemo.getInstance();
            }, String.valueOf(i)).start();
        }
    }

}


```

