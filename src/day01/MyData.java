package day01;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenxiaonuo
 * @date 2019-08-07 11:37
 */
class MyData{
    volatile int number = 0;

    public void addT060(){
        this.number = 60;
    }

    public void addPlusPlus(){
        //number++在多线程下是非线程安全的
        number++;
    }

    AtomicInteger atomicInteger = new AtomicInteger();
    public void addAtomic(){
        atomicInteger.getAndIncrement();
    }

    public void mySort(){
        int x = 11;//语句1
        int y = 12;//语句2
        x = x + 5;//语句3
        y = x * x;//语句4
    }
}
