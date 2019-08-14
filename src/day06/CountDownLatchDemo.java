package day06;

import java.util.concurrent.CountDownLatch;

/**
 * @author chenxiaonuo
 * @date 2019-08-14 14:24
 */
public class CountDownLatchDemo {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(6);

        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 国，被灭");
                countDownLatch.countDown();
            }, CountryEnum.forEach_CountryEnum(i+1).getRetMessage()).start();
        }
        try {
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName() +" **********秦帝国，一统华夏");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void closeDoow(){
        CountDownLatch countDownLatch = new CountDownLatch(6);

        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 上晚自习，离开教室");
                countDownLatch.countDown();
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName() +" **********班长最后关门走人");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


