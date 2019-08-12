package day04;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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
