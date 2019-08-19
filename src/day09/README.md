## 死锁编码及定位分析
&#8194;&#8194;&#8194;&#8194;死锁是指两个或两个以上的进行在执行过程中，因争夺资源而造成的一种互相等待的现象，若无外力干涉那么他们将无法推进下去，如果系统资源充足，进程的资源请求都能得到满足，死锁出现的可能性就很低，否则就会因争夺有限的资源而陷入死锁。
#### 产生死锁的主要原因
- 系统资源不足
- 进程运行推进的顺序不合适
- 资源分配不当
#### 定位分析
```java
public class DeadLockDemo {

    public static void main(String[] args) {
        String lockA = "lockA";
        String lockB = "lockB";

        new Thread(new HoldLockThread(lockA,lockB), "t1").start();
        new Thread(new HoldLockThread(lockB,lockA), "t2").start();
/*
            linux   ps -ef | grep xxxx
            windows下的java运行程序，也有类似ps的查看进程的命令
         */
    }
}

class HoldLockThread implements  Runnable{

    private String lockA;
    private String lockB;

    public HoldLockThread(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName() + " 自己持有：" + lockA + " 尝试获取" + lockB);

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (lockB){
                System.out.println(Thread.currentThread().getName() + " 自己持有：" + lockB + " 尝试获取" + lockA);
            }

        }
    }
}
```
- jps命令定位进程号
```java
G:\idea-workspace\interview>jps -l

4832 org.jetbrains.kotlin.daemon.KotlinCompileDaemon
9584
3188 day09.DeadLockDemo
9396 sun.tools.jps.Jps
4328 org.jetbrains.jps.cmdline.Launcher
```
- jstack找到死锁查看
```java
G:\idea-workspace\interview>jstack 3188

Java stack information for the threads listed above:
===================================================
"t2":
        at day09.HoldLockThread.run(DeadLockDemo.java:47)
        - waiting to lock <0x000000078068d790> (a java.lang.String)
        - locked <0x000000078068d7c8> (a java.lang.String)
        at java.lang.Thread.run(Thread.java:748)
"t1":
        at day09.HoldLockThread.run(DeadLockDemo.java:47)
        - waiting to lock <0x000000078068d7c8> (a java.lang.String)
        - locked <0x000000078068d790> (a java.lang.String)
        at java.lang.Thread.run(Thread.java:748)

Found 1 deadlock.
```
