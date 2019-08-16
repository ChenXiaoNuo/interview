package day07;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
