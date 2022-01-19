package com.laowang.test;

import java.util.ArrayList;
import java.util.List;

/*
* 对于wait() notify() ，应该在synchronized代码块中，因为只有拿到锁了，才能执行这两个方法，否则报错
* */
public class T02_waitNotify {

    private final List<Object> list = new ArrayList<>();

    public void add(Object o){
        list.add(o);
    }

    public int size(){
        return list.size();
    }

    public static void main(String[] args) {

        T02_waitNotify test = new T02_waitNotify();

        Object lock = new Object();
        //观察者线程
        new Thread(()->{
            //尝试lock
            synchronized (lock){
                System.out.println("t1 启动。。。"); //进来这里就说明拿到了锁
                if (test.size() != 5){
                    try {
                        lock.wait();//size没到5 之前，t1 都在waiting阻塞状态
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lock.notify();// 让t2继续执行
                System.out.println("t1结束了");
            }
        },"t1").start();

        //执行者线程
        new Thread(()->{
            synchronized (lock){
                System.out.println("t2启动");
                for (int i = 0; i < 10; i++) {
                    test.add(new Object());
                    System.out.println("add " + i);
                    if (test.size() == 5){
                        lock.notify();//光通知没用，锁还没被释放
                        try {
                            lock.wait();//还得主动出让锁才行
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("t2结束了");
            }
        },"t2").start();
    }
}
