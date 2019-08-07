package day01;

import java.util.concurrent.TimeUnit;

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


        while (myData.number == 0){
            //main线程一直在这里等待循环，直到number值不再等于0.
        }

        System.out.println(Thread.currentThread().getName() + "\tmission is over，main get number value：" + myData.number);
    }


}

