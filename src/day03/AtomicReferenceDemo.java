package day03;

import java.util.concurrent.atomic.AtomicReference;

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
