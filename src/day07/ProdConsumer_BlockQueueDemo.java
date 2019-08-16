package day07;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者消费者队列版
 * @author hutao
 * @date 2019-08-16 10:23
 */
public class ProdConsumer_BlockQueueDemo {

    public static void main(String[] args) throws Exception {
        MyResource myResource = new MyResource(new ArrayBlockingQueue<>(10));
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " 生产线程启动");
            try {
                myResource.myProd();
                System.out.println();
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Prod").start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " 消费线程启动");
            try {
                myResource.myConsumer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Consumer").start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("5秒钟时间到，main线程叫停，活动结束");
        myResource.stop();
    }
}

class MyResource{

    private volatile boolean flag = true;//默认开启，进行生产消费
    private AtomicInteger atomicInteger = new AtomicInteger();
    BlockingQueue<String> blockingQueue = null;

    public MyResource(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println(blockingQueue.getClass().getName());
    }

    public void myProd() throws Exception {
        String data = null;
        boolean retValue;

        while (flag) {
            data = atomicInteger.incrementAndGet() + "";
            retValue = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (retValue){
                System.out.println(Thread.currentThread().getName() + " 插入队列" + data + "成功");
            } else {
                System.out.println(Thread.currentThread().getName() + " 插入队列" + data + "失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println(Thread.currentThread().getName() + " 被叫停了！表示flag=false，生产动作结束");
    }

    public void myConsumer() throws Exception {
        String result;
        while (flag) {
            result = blockingQueue.poll(2L, TimeUnit.SECONDS);
            if (null == result || result.equalsIgnoreCase("")){
                flag = false;
                System.out.println(Thread.currentThread().getName() + " 超过2秒钟没有取到，消费队列退出");
                return;
            }
            System.out.println(Thread.currentThread().getName() + " 消费队列" + result + "成功");
        }
    }

    public void stop() throws Exception {
        this.flag = false;
    }

}
