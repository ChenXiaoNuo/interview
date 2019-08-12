## ABA问题

#### ABA问题怎么产生的
&#8194;&#8194;&#8194;&#8194;CAS会导致"ABA"问题。  
&#8194;&#8194;&#8194;&#8194;CAS算法实现的一个重要前提需要拉取出内存中某时刻的数据并在当下时刻比较并替换，那么这个时间差会导致数据的变化。
比如说一个线程t1从内存位置V取出A，这时候另一个线程t2也行内存中取出A，并且线程t2进行了一些操作将值变为了B，然后线程t2又将V位置的数据变为A，
这时候线程t1进行CAS操作发现内存中仍然是A，然后线程one操作成功。  
&#8194;&#8194;&#8194;&#8194;尽管线程t1的CAS操作成功，但是不代表这个过程就是没有问题的。

- AtomicReference原子引用
```java
/**
 * AtomicReference原子引用
 * @author chenxiaonuo
 * @date 2019-08-12 10:17
 */
public class AtomicReferenceDemo {

    public static void main(String[] args) {
        User user1 = new User("张三", 20);
        User user2 = new User("李四", 31);

        AtomicReference<User> atomicReference = new AtomicReference<>();
        atomicReference.set(user1);

        System.out.println(atomicReference.compareAndSet(user1, user2) + " " + atomicReference.get().toString());
        System.out.println(atomicReference.compareAndSet(user1, user2) + " " + atomicReference.get().toString());
    }
}


class User{
    String userName;
    int age;

    public User(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}
```

- AtomicStampedReference版本号原子引用
```java
/**
 * AtomicStampedReference 解决ABA问题
 * @author chenxiaonuo
 * @date 2019-08-12 10:39
 */
public class ABADemo {

    static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100, 1);


    public static void main(String[] args) {

        System.out.println("============以下是ABA问题的产生===============");

        new Thread(() -> {
            atomicReference.compareAndSet(100, 101);
            atomicReference.compareAndSet(101, 100);
        }, "t1").start();

        new Thread(() -> {
            try {
                //暂停1s，保证t1先完成一次ABA操作
                TimeUnit.SECONDS.sleep(1);
                System.out.println(atomicReference.compareAndSet(100, 2019) + " " + atomicReference.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

        System.out.println("============以下是ABA问题的解决===============");

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();//t3线程版本号
            System.out.println(Thread.currentThread().getName() + " 第一次版本号：" + stamp);
            try {
                //暂停2秒钟t3线程
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicStampedReference.compareAndSet(100,101,atomicStampedReference.getStamp(),atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + " 第二次版本号：" + stamp);
            atomicStampedReference.compareAndSet(101,100,atomicStampedReference.getStamp(),atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + " 第三次版本号：" + stamp);
        }, "t3").start();



        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + " 第一次版本号：" + stamp);

            try {
                //暂停3s，保证t3先完成一次ABA操作
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean b = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + " 修改成功否：" + b + " 当前最新实际的版本号为："  + atomicStampedReference.getStamp());
            System.out.println(Thread.currentThread().getName() + " 当前实际最新值" + atomicStampedReference.getReference());
            }, "t4").start();


    }

}
```
