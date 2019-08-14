package day07;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ArrayBlockingQueue：是一个基于数组结构的有界阻塞队列，此队列按 FIFO(先进先出)原则对元素进行排序。
 * LinkedBlockingQueue：一个基于链表结构的阻塞队列，此队列按 FIFO(先进先出)排序元素，吞吐量通常要高于ArrayBlockingQueue。
 * SynchronousQueue：一个不存储元素的阻塞队列。每个插入操作必须等到另一个线程调用移除操作，否则插入操作一直处于阻塞状态
 * @author chenxiaonuo
 * @date 2019-08-14 16:22
 */
public class BlockingQueueDemo {

    public static void main(String[] args) throws Exception {
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);

        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        //System.out.println(blockingQueue.add("c"));
        //System.out.println(blockingQueue.offer("c"));//false
        //blockingQueue.put("c");//一直阻塞
        blockingQueue.offer("c", 2L, TimeUnit.SECONDS);//阻塞2s

        System.out.println(blockingQueue.element());//是否为空，并且输出队首元素

        System.out.println(blockingQueue.remove());//先进先出
        System.out.println(blockingQueue.remove());
        //System.out.println(blockingQueue.remove());
        //System.out.println(blockingQueue.poll());//null
        //blockingQueue.take();//一直阻塞
    }
}
