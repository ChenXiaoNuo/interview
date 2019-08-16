package day08;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * java多线程中，第三种获取多线程的方式 Callable接口
 * @author chenxiaonuo
 * @date 2019-08-16 11:21
 */
public class CallableDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(new MyThread());

        Thread t1 = new Thread(futureTask, "AA");//多个线程使用同一个futureTask，只计算一次
        t1.start();

        int result01 = 100;
        while (!futureTask.isDone()){

        }
        int result02 = futureTask.get();
        System.out.println("result: " + (result01 + result02));
    }
}

class MyThread implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("***** coming  Callable");
        return 1024;
    }
}