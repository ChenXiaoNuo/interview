## 阻塞队列知道吗？
- 阻塞队列，顾名思义，首先它是一个队列，而一个阻塞队列在数据结构中所起的作用大致如下图所示：
![阻塞队列](阻塞队列.png)  
- 当阻塞队列是空时，从队列中获取元素的操作将会被阻塞。  当阻塞队列是满时，往队列里添加元素的操作将会被阻塞。
- 试图从空的阻塞队列中获取元素的线程将会被阻塞，知道其他的线程往空的队列插入新的元素。同样，试图往已满的阻塞队列中添加新元素的线程同样也会被阻塞，直到其他的线程
从列中移除一个或者多个元素或者完全清空队列后使队列重新变得空闲起来并后续新增。

#### 为什么用，有什么好处？
- 在多线程领域：所谓阻塞，在某些情况下会挂起线程(即阻塞)，一旦条件满足，被挂起的线程又会自动被唤醒
- 好处是我们不需要关心什么时候需要阻塞线程，什么时候需要唤醒线程，因为这一切BlockingQueue都一手包办了；在 concurrent 包发布以前，在多线程环境下，我们每个程序员都必须去自己控制这些细节，尤其还要兼顾效率和线程安全，会给我们程序带来不小的复杂度  

#### 架构梳理与种类分析  
![队列](队列.png)
  - 种类分析
    - ArrayBlockingQueue：由数组结构组成的有界阻塞队列。
    - LinkedBlockingQueue：由链表结构组成的有界(大小默认为Integer.MAX_VALUE)阻塞队列。
    - PriorityBlockingQueue：支持优先级排序的无界阻塞队列。
    - DelayBlockingQueue：使用优先级队列实现的延迟无界阻塞队列。
    - SynchronousQueue：不存储元素的阻塞队列，也即单个元素的队列。
    - LinkedTransferQueue：由链表结构组成的无界阻塞队列。
    - LinkedBlockingDeque：由链表结构组成的双向阻塞队列。
    
 - BlockingQueue的核心方法  
|方法类型|抛出异常|特殊值|阻塞|超时|  
:-:|:-:|:-:|:-:|:-:  
|插入|add(e)|offer(e)|put(e)|offer(e,time,unit)|  
|移除|remove()|poll()|take()|poll(time,unit)|  
|检查|element()|peek()|不可用|不可用|  
    - 抛出异常
        - 当阻塞队列满时，再往队列里add插入元素会抛出 java.lang.IllegalStateException: Queue full；  
        - 当阻塞队列空时，再从队列里remove移除元素会抛出 java.util.NoSuchElementException
    - 特殊值
        - 插入方法，成功true失败false
        - 移除方法，成功返回出队列的元素，队列里面没有就返回null
    - 一直阻塞
        - 当阻塞队列满时，生产者线程继续往队列里put元素，队列会一直阻塞生产线程知道put数据或者响应中断退出
        - 当阻塞队列空时，消费者线程试图从队列里take元素，队列会一直阻塞消费者线程知道队列可用
    - 超时退出
        - 当阻塞队列满时，队列会阻塞生产者现场一定时间，超过限时后生产者线程会退出
        
```java

/**
 * ArrayBlockingQueue：是一个基于数组结构的有界阻塞队列，此队列按 FIFO(先进先出)原则对元素进行排序。
 * LinkedBlockingQueue：一个基于链表结构的阻塞队列，此队列按 FIFO(先进先出)排序元素，吞吐量通常要高于ArrayBlockingQueue。
 * SynchronousQueue：一个不存储元素的阻塞队列。每个插入操作必须等到另一个线程调用移除操作，否则插入操作一直处于阻塞状态
 * @author chenxiaonuo
 * @date 2019-08-14 16:22
 */
public class BlockingQueueDemo {

    public static void main(String[] args) throws Exception {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);

        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        //System.out.println(blockingQueue.add("c"));
        //System.out.println(blockingQueue.offer("c"));//false
        //blockingQueue.put("c");//一直阻塞
        blockingQueue.offer("c", 2L, TimeUnit.SECONDS);//阻塞2s

        System.out.println(blockingQueue.element());//是否为空，并且输出队首元素

        System.out.println(blockingQueue.remove());//先进先出
        System.out.println(blockingQueue.remove());
        //System.out.println(blockingQueue.remove());
        //System.out.println(blockingQueue.poll());//null
        //blockingQueue.take();//一直阻塞
    }
}
```
- SynchronousQueue 没有容量。与其他BlockingQueue不同，SynchronousQueue 是一个不存储元素的 BlockingQueue。每一个put操作必须等待一个take操作，否则不能继续添加元素，反之亦然。
```java
/**
* 同步队列
*/
public class SynchronousQueueDemo {

    public static void main(String[] args) {

        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " put 1");
                blockingQueue.put("1");
                System.out.println(Thread.currentThread().getName() + " put 2");
                blockingQueue.put("2");
                System.out.println(Thread.currentThread().getName() + " put 3");
                blockingQueue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + " " + blockingQueue.take());
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + " " + blockingQueue.take());
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + " " + blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

    }
}
```
#### 应用场景
- 线程通信之生产者消费者传统版
```java
/**
 * 传统版生产者消费者模式
 * 一个初始值为零的变量，两个线程对其交替操作，一个加1一个减1，来5轮
 * @author chenxiaonuo
 * @date 2019-08-15 10:34
 */
public class ProdConsumer_TraditionDemo {

    public static void main(String[] args) {
        ShareData shareData = new ShareData();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    shareData.increament();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    shareData.decrement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "t2").start();
    }
}

class ShareData{

    private int number = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increament() throws Exception {
        lock.lock();
        try {
            //判断
            while (number != 0){
                //等待，不能生产
                condition.await();
            }
            //增加1
            number++;
            System.out.println(Thread.currentThread().getName() + " " + number);
            //通知唤醒
            condition.signalAll();;
        } finally {
            lock.unlock();
        }
    }

    public void decrement() throws Exception {
        lock.lock();
        try {
            //判断
            while (number == 0){
                //等待，不能消费
                condition.await();
            }
            //减少1
            number--;
            System.out.println(Thread.currentThread().getName() + " " + number);
            //通知唤醒
            condition.signalAll();;
        } finally {
            lock.unlock();
        }
    }
}
```
- synchronized 和 Lock 有什么区别？用新的Lock有什么好处？  
   1. 原始构成
       - synchronized 是关键字属于 JVM 层面
           monitorenter(底层是通过 monitor 对象来完成，其实 wait/notify等方法也依赖于monitor对象只有在同步块或方法中才能wait/notify等方法)
           monitorexit
       - Lock 是具体类(java.util.concurrent.locks.Lock) 是api层面的锁
   2. 使用方法
       - synchronized 不需要用户去手动释放锁，当 synchronized 代码执行完成后系统会自动让线程释放对锁的占用
       - ReentrantLock 则需要用户去手动释放锁，若没有主动释放锁，就有可能导致出现死锁现象。需要 lock()和unLock()方法配置tru/finally语句块来完成
   3. 等待是否可断
       - synchronized 不可中断，除非抛出异常或者正常运行完成
       - ReentrantLock 可中断，a.设置超时方法 tryLock(long timeout,TimeUnit unit)
                               b.lockInterruptibly() 放代码块，调用 interrupt() 方法可中断
   4. 加锁是否公平
       - synchronized 非公平锁
       - ReentrantLock 两者都可以，默认非公平锁，构造方法可以传入boolean值，true为公平锁，false为非公平锁
 
   5. 锁绑定多个条件Condition
       - synchronized 没有
       - ReentrantLock 用来实现分组唤醒需要唤醒的线程们，可以精确唤醒，而不是像synchronized要么随机唤醒一个线程要么唤醒全部线程。
 
- 锁绑定多个条件Condition
```java

public class SyncAndReetrantLockDemo {

    public static void main(String[] args) {

        /*
            多线程之间按顺序调用，实现A->B->C三个线程启动，要求如下：
            AA打印5次，BB打印10次，CC打印15次...共10轮
         */
        ShareResource shareResource = new ShareResource();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                shareResource.print(5,shareResource.getC1(),shareResource.getC2(),2);
            }
        }, "A").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                shareResource.print(10,shareResource.getC2(),shareResource.getC3(),3);
            }
        }, "B").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                shareResource.print(15,shareResource.getC3(),shareResource.getC1(),1);
            }
        }, "C").start();

    }
}

class ShareResource{

    private int number = 1;//A:1 B:2 C:3
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    public Condition getC1() {
        return c1;
    }

    public Condition getC2() {
        return c2;
    }

    public Condition getC3() {
        return c3;
    }

    public void print(int times, Condition condition1, Condition condition2, int num){
        lock.lock();
        try {
            int temp = num == 1 ? 3 : (num-1);
            while (number != temp){
                condition1.await();
            }
            for (int i = 0; i < times; i++) {
                System.out.println(Thread.currentThread().getName() + " " + i);
            }
            //通知标志
            this.number = num;
            condition2.signal();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```
- 生产者消费者队列版
```java
public class ProdConsumer_BlockQueueDemo {

    public static void main(String[] args) throws Exception {
        MyResource myResource = new MyResource(new ArrayBlockingQueue<>(10));
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " 生产线程启动");
            try {
                myResource.myProd();
                System.out.println();
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Prod").start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " 消费线程启动");
            try {
                myResource.myConsumer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Consumer").start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("5秒钟时间到，main线程叫停，活动结束");
        myResource.stop();
    }
}

class MyResource{

    private volatile boolean flag = true;//默认开启，进行生产消费
    private AtomicInteger atomicInteger = new AtomicInteger();
    BlockingQueue<String> blockingQueue = null;

    public MyResource(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println(blockingQueue.getClass().getName());
    }

    public void myProd() throws Exception {
        String data = null;
        boolean retValue;

        while (flag) {
            data = atomicInteger.incrementAndGet() + "";
            retValue = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (retValue){
                System.out.println(Thread.currentThread().getName() + " 插入队列" + data + "成功");
            } else {
                System.out.println(Thread.currentThread().getName() + " 插入队列" + data + "失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println(Thread.currentThread().getName() + " 被叫停了！表示flag=false，生产动作结束");
    }

    public void myConsumer() throws Exception {
        String result;
        while (flag) {
            result = blockingQueue.poll(2L, TimeUnit.SECONDS);
            if (null == result || result.equalsIgnoreCase("")){
                flag = false;
                System.out.println(Thread.currentThread().getName() + " 超过2秒钟没有取到，消费队列退出");
                return;
            }
            System.out.println(Thread.currentThread().getName() + " 消费队列" + result + "成功");
        }
    }

    public void stop() throws Exception {
        this.flag = false;
    }

}
```
