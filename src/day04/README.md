## 集合类不安全

```java
/**
 * 集合类不安全问题
 *  ArrayList
 *  HasHsET
 * @author chenxiaonuo
 * @date 2019-08-12 11:00
 */
public class ContainerNotSafeDemo {

    public static void main(String[] args) {
        Map<String,String> map = new ConcurrentHashMap<>();//Collections.synchronizedMap(new HashMap<>());//new ConcurrentHashMap<>();//new HashMap<>();
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 8));
                System.out.println(map);
            }, i + "线程" ).start();
        }

    }

    public static void setNotSafe(){
        Set<String> set = new CopyOnWriteArraySet<>();//Collections.synchronizedSet(new HashSet<>());
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                set.add(UUID.randomUUID().toString().substring(0, 8));
                System.out.println(set);
            }, i + "线程" ).start();
        }
    }

    public static void listNotSafe(){
        List<String> list = new ArrayList<>();

        /*
            1 故障现象
                java.util.ConcurrentModificationException-并发异常
            2 导致原因
                并发争抢修改导致
            3 解决方案
                3.1 new Vector<>()
                3.2 Collections.synchronizedList()
                3.3 new CopyOnWriteArrayList<>()
            4 优化建议
         */
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 8));
                System.out.println(list);
            }, i + "线程" ).start();
        }
    }
}

```

```java
    /*
        CopyOnWrite容器即写时复制的容器。往一个容器添加元素的时候，不直接往当前容器 Object[] 添加，而是先将当前容器 Object[] 进行copy，
        赋值出一个新的容器Object[] newElements，然后新的容器 Object[] newElements添加元素，添加元素完成之后，再将原容器的引用指向新的容器 setArray(newElements)。
        这样做的好处就是可以对 CopyOnWrite 容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。CopyOnWrite 也是一种读写分离的思想，读与写不同的容器
     */
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }
```

