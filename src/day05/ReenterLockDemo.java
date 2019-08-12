package day05;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
