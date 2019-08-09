package day02;

import java.util.concurrent.atomic.AtomicInteger;

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

        atomicInteger.getAndIncrement();
    }


}
