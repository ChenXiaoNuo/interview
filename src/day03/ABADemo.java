package day03;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * AtomicStampedReference 解决ABA问题
 * @author chenxiaonuo
 * @date 2019-08-12 10:39
 */
public class ABADemo {

    static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100, 1);


    public static void main(String[] args) {

        System.out.println("============以下是ABA问题的产生===============");

        new Thread(() -> {
            atomicReference.compareAndSet(100, 101);
            atomicReference.compareAndSet(101, 100);
        }, "t1").start();

        new Thread(() -> {
            try {
                //暂停1s，保证t1先完成一次ABA操作
                TimeUnit.SECONDS.sleep(1);
                System.out.println(atomicReference.compareAndSet(100, 2019) + " " + atomicReference.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

        System.out.println("============以下是ABA问题的解决===============");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();//t3线程版本号
            System.out.println(Thread.currentThread().getName() + " 第一次版本号：" + stamp);
            try {
                //暂停2秒钟t3线程
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicStampedReference.compareAndSet(100,101,atomicStampedReference.getStamp(),atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + " 第二次版本号：" + stamp);
            atomicStampedReference.compareAndSet(101,100,atomicStampedReference.getStamp(),atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + " 第三次版本号：" + stamp);
        }, "t3").start();



        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + " 第一次版本号：" + stamp);

            try {
                //暂停3s，保证t3先完成一次ABA操作
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean b = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + " 修改成功否：" + b + " 当前最新实际的版本号为："  + atomicStampedReference.getStamp());
            System.out.println(Thread.currentThread().getName() + " 当前实际最新值" + atomicStampedReference.getReference());
            }, "t4").start();


    }

}
