package day07;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * synchronized 和 Lock 有什么区别？用新的Lock有什么好处？
 *  1.原始构成
 *      synchronized 是关键字属于 JVM 层面
 *          monitorenter(底层是通过 monitor 对象来完成，其实 wait/notify等方法也依赖于monitor对象只有在同步块或方法中才能wait/notify等方法)
 *          monitorexit
 *      Lock 是具体类(java.util.concurrent.locks.Lock) 是api层面的锁
 *  2.使用方法
 *      synchronized 不需要用户去手动释放锁，当 synchronized 代码执行完成后系统会自动让线程释放对锁的占用
 *      ReentrantLock 则需要用户去手动释放锁，若没有主动释放锁，就有可能导致出现死锁现象。需要 lock()和unLock()方法配置tru/finally语句块来完成
 *  3.等待是否可断
 *      synchronized 不可中断，除非抛出异常或者正常运行完成
 *      ReentrantLock 可中断，a.设置超时方法 tryLock(long timeout,TimeUnit unit)
 *                            b.lockInterruptibly() 放代码块，调用 interrupt() 方法可中断
 *  4.加锁是否公平
 *      synchronized 非公平锁
 *      ReentrantLock 两者都可以，默认非公平锁，构造方法可以传入boolean值，true为公平锁，false为非公平锁
 *
 *  5.锁绑定多个条件Condition
 *      synchronized 没有
 *      ReentrantLock 用来实现分组唤醒需要唤醒的线程们，可以精确唤醒，而不是像synchronized要么随机唤醒一个线程要么唤醒全部线程。
 *
 * @author chenxiaonuo
 * @date 2019-08-15 10:59
 */
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
