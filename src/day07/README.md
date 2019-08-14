## 阻塞队列知道吗？
- 阻塞队列，顾名思义，首先它是一个队列，而一个阻塞队列在数据结构中索契的作用大致如下图所示：
![阻塞队列](阻塞队列.png)  
- 当阻塞队列是空时，从队列中获取元素的操作将会被阻塞。  当阻塞队列是满时，往队列里添加元素的操作将会被阻塞。
- 试图从空的阻塞队列中获取元素的线程将会被阻塞，知道其他的线程往空的队列插入新的元素。同样，试图往已满的阻塞队列中添加新元素的线程同样也会被阻塞，直到其他的线程
从列中移除一个或者多个元素或者完全清空队列后使队列重新变得空闲起来并后续新增。

#### 为什么用，有什么好处？
- 在多线程领域：所谓阻塞，在某些情况下会挂起线程(即阻塞)，一旦条件满足，被挂起的线程又会自动被唤醒
- 好处是我们不需要关心什么时候需要阻塞线程，什么时候需要唤醒线程，因为这一切BlockingQueue都一手包办了；在 concurrent 包发布以前，在多线程环境下，我们每个程序员都必须去自己控制这些细节，尤其还要兼顾效率和线程安全，会给我们程序带来不小的复杂度  

#### 架构梳理与种类分析  
![队列](队列.png)
  - 种类分析
    - ArrayBlockingQueue：由数组结构组成的有界阻塞队列。
    - LinkedBlockingQueue：由链表结构组成的有界(大小默认为Integer.MAX_VALUE)阻塞队列。
    - PriorityBlockingQueue：支持优先级排序的无界阻塞队列。
    - DelayBlockingQueue：使用优先级队列实现的延迟无界阻塞队列。
    - SynchronousQueue：不存储元素的阻塞队列，也即单个元素的队列。
    - LinkedTransferQueue：由链表结构组成的无界阻塞队列。
    - LinkedBlockingDeque：由链表结构组成的双向阻塞队列。
    
 - BlockingQueue的核心方法  
|方法类型|抛出异常|特殊值|阻塞|超时|  
:-:|:-:|:-:|:-:|:-:  
|插入|add(e)|offer(e)|put(e)|offer(e,time,unit)|  
|移除|remove()|poll()|take()|poll(time,unit)|  
|检查|element()|peek()|不可用|不可用|  
    - 抛出异常
        - 当阻塞队列满时，再往队列里add插入元素会抛出 java.lang.IllegalStateException: Queue full；  
        - 当阻塞队列空时，再从队列里remove移除元素会抛出 java.util.NoSuchElementException
    - 特殊值
        - 插入方法，成功true失败false
        - 移除方法，成功返回出队列的元素，队列里面没有就返回null
    - 一直阻塞
        - 当阻塞队列满时，生产者线程继续往队列里put元素，队列会一直阻塞生产线程知道put数据或者响应中断退出
        - 当阻塞队列空时，消费者线程试图从队列里take元素，队列会一直阻塞消费者线程知道队列可用
    - 超时退出
        - 当阻塞队列满时，队列会阻塞生产者现场一定时间，超过限时后生产者线程会退出
        
```java

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

```
- SynchronousQueue 没有容量。与其他BlockingQueue不同，SynchronousQueue 是一个不存储元素的 BlockingQueue。每一个put操作必须等待一个take操作，否则不能继续添加元素，反之亦然。
```java
/**
* 同步队列
*/
public class SynchronousQueueDemo {

    public static void main(String[] args) {

        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " put 1");
                blockingQueue.put("1");
                System.out.println(Thread.currentThread().getName() + " put 2");
                blockingQueue.put("2");
                System.out.println(Thread.currentThread().getName() + " put 3");
                blockingQueue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + " " + blockingQueue.take());
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + " " + blockingQueue.take());
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + " " + blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

    }
}

```
#### 应用场景
