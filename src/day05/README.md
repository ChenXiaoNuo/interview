## Java 中的锁你知道哪些？

#### 公平和非公平锁
- 公平锁  
&#8194;&#8194;&#8194;&#8194;是指多个线程申请锁的顺序来获取锁，类似排队，先来后到。  
&#8194;&#8194;&#8194;&#8194;在并发环境中，每个线程在获取锁时会查看此锁维护的等待队列，如果为空，或者当前线程是等待队列的第一个，就占有锁，
否则就会加入到等待队列中，以后会按照FIFO的规则从队列中取到自己。

- 非公平锁  
&#8194;&#8194;&#8194;&#8194;是指多个线程获取锁的顺序不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁；
在高并发的情况下，有可能会造成优先级反转或者接现象  
&#8194;&#8194;&#8194;&#8194;非公平锁比较粗鲁，上来就直接尝试占有锁，如果尝试失败，就再采用类似公平锁那种方式

  
&#8194;&#8194;&#8194;&#8194;并发包中的 ReentrantLock的创建可以指定构造函数的boolean类型来得到公平锁或非公平锁，默认是非公平锁；非公平锁的有点在于吞吐量比公平锁大。
对于synchronized而言，也是一种非公平锁。

#### 可重入锁(递归锁)
&#8194;&#8194;&#8194;&#8194;指的是统一线程外层函数获取锁之后，内层递归函数仍然能获取该锁的代码，在同一个线程在外层方法获取锁的时候，
在进入内层方法会自动获取锁。也就是说，**线程可以进入任何一个它已经拥有的锁所同步着的代码块**。

ReentrantLock/Synchronized 就是典型的可重入锁，可重入锁最大的作用是**避免死锁**

```java
/**
 * 结果1：Synchronized 就是典型的可重入锁
 * t1 invoke sendSms()
 * t1 ####invoke sendEmail()
 * t2 invoke sendSms()
 * t2 ####invoke sendEmail()
 *
 * 结果2：ReentrantLock 就是典型的可重入锁
 * Thread-0 invoke get()
 * Thread-0 ----invoke set()
 * Thread-1 invoke get()
 * Thread-1 ----invoke set()
 *
 * @author chenxiaonuo
 * @date 2019-08-12 15:06
 */
public class ReenterLockDemo {

    public static void main(String[] args) {
        Phone phone = new Phone();
        new Thread(() -> {
            phone.sendSms();
        }, "t1").start();

        new Thread(() -> {
            phone.sendSms();
        }, "t2").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println();
        System.out.println();

        Thread t3 = new Thread(phone);
        Thread t4 = new Thread(phone);

        t3.start();
        t4.start();
    }

}

class Phone implements Runnable{

    public synchronized  void sendSms(){
        System.out.println(Thread.currentThread().getName() + " invoke sendSms()");
        sendEmail();
    }

    public synchronized  void sendEmail(){
        System.out.println(Thread.currentThread().getName() + " ####invoke sendEmail()");
    }

    Lock lock = new ReentrantLock();
    @Override
    public void run() {
        get();
    }

    public void get(){
        lock.lock();
        try{
            System.out.println(Thread.currentThread().getName() + " invoke get()");
            set();
        } finally {
            lock.unlock();
        }
    }
    public void set(){
        lock.lock();
        try{
            System.out.println(Thread.currentThread().getName() + " ----invoke set()");
        } finally {
            lock.unlock();
        }
    }
}
```
#### 自旋锁
&#8194;&#8194;&#8194;&#8194;自旋锁(spinlock)：是指尝试获取锁的线程不会立即阻塞，而是**采用循环的方式去尝试获取锁**，这样的好处是减少上下文切换的消耗，缺点就是循环会小小CPU

#### 独占锁/共享锁

