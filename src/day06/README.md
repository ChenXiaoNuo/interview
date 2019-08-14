## CountDownLatch/CyclicBarrier/Semaphore 使用过吗？
##### CountDownLatch  
- 让一些线程阻塞直到另一些线程完成一系列操作后才被唤醒  
- CountDownLatch主要有两个方法，当一个或多个线程调用await方法时，调用线程会被阻塞。其他线程调用countDown方法会将计算器减1(调用countDown方法的线程不会阻塞),
当计算器的值变为零时，因调用await方法被阻塞的线程会被唤醒，继续执行。
```java
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

public enum CountryEnum {

    ONE(1,"齐"),
    TWO(2,"楚"),
    THREE(3,"燕"),
    FOUR(4,"赵"),
    FIVE(5,"魏"),
    SIX(6,"韩");

    private Integer retcode;
    private String retMessage;

    CountryEnum(Integer retcode, String retMessage) {
        this.retcode = retcode;
        this.retMessage = retMessage;
    }

    public Integer getRetcode() {
        return retcode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public static CountryEnum forEach_CountryEnum(int index){
        CountryEnum[] values = CountryEnum.values();
        for (CountryEnum value : values) {
            if (index == value.getRetcode()){
                return value;
            }
        }
        return null;
    }
}
```

##### CyclicBarrier
- CyclicBarrier 的字面意思是可循环(Cyclic)使用的屏障(Barrier)。它要做的事情是，让一组线程到达一个屏障(也可以叫同步点)时被阻塞，知道最后一个线程到达屏障时，
屏障才会开门，所有被屏障拦截的线程才会继续干活，线程进入屏障通过CyclicBarrier的await()方法。
```java
public class CyclicBarrierDemo {

    public static void main(String[] args) {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, ()->{
            System.out.println("********召唤神龙");
        });

        for (int i = 0; i < 7; i++) {
            final int temp = i+1;
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 收集到第" + temp + "龙珠");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}

```
##### Semaphore
- 信号量主要用于两个目的：一个是用于多个共享资源的互斥使用，另一个用于并发线程数的控制。
```java
public class SemaphoreDemo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);//模拟3个停车位

        for (int i = 0; i < 6; i++) {//模拟6部汽车
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " 抢到车位！");
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(Thread.currentThread().getName() + " 停车3秒后离开车位！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }, String.valueOf(i)).start();
        }
    }
}



```