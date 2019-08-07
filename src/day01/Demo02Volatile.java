package day01;

/**
 * 验证volatile不保证原子性
 *  1.原子性指的是什么意思？
 *      不可分割，完整性，也即某个线程正在做某个业务时，中间不可以被加塞或者被分割。
 *      需要整体完整，要么同时成功，要么同时失败
 *  2.volatile不保证原子性的案例演示
 *  3.如何解决
 *      * 加sync
 *      * 利用Atomic
 * @author chenxiaonuo
 * @date 2019-08-06 17:15
 */
public class Demo02Volatile {

    public static void main(String[] args) {
        MyData myData = new MyData();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myData.addPlusPlus();
                    myData.addAtomic();
                }
            }, String.valueOf(i)).start();
        }

        //需要等待上面的20个线程全部计算完成后，再用main线程取得最终的结果值
        while (Thread.activeCount() > 2){
            Thread.yield();
        }

        //出现了丢失写值的情况，写覆盖
        System.out.println(Thread.currentThread().getName() + " int type, finally number value：" + myData.number);
        //结果正确
        System.out.println(Thread.currentThread().getName() + " AtomicInteger type, finally number value：" + myData.atomicInteger);

    }


}

