package day08;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Executors.newScheduledThreadPool()
 * java8新出：Executors.newWorkStealingPool(int)-使用目前机器上可用的处理器作为它的并行级别
 *
 * @author chenxiaonuo
 * @date 2019-08-16 16:34
 */
public class ThreadPoolDemo {

    public static void main(String[] args) {
        //ExecutorService executorService = Executors.newFixedThreadPool(5);//固定5个线程的池子
        //ExecutorService executorService = Executors.newSingleThreadExecutor();//一个线程的池子
        ExecutorService executorService = Executors.newCachedThreadPool();//N个线程的池子

        //模拟10个用户来办理业务
        try {
            for (int i = 0; i < 20; i++) {
                executorService.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "  办理业务！");
                });
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }


    }


}
