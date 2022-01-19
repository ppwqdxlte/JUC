package com.laowang.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class T04_LockSupport {

    private List<Object> list = new ArrayList<>();

    public void add(Object o){
        list.add(o);
    }
    public int size(){
        return list.size();
    }

    private static Thread t1,t2;

    public static void main(String[] args) {
        T04_LockSupport test = new T04_LockSupport();
        //观察者
        t1 = new Thread(() -> {
            System.out.println("t1 start:");
            LockSupport.park();
            System.out.println("t1 end.");
            LockSupport.unpark(t2);
        }, "t1");
        //操作者
        t2 = new Thread(() -> {
            System.out.println("t2 start:");
            for (int i = 0; i < 10; i++) {
                test.add(new Object());
                System.out.println("add "+i);
                if (test.size() == 5){
                    LockSupport.unpark(t1);
                    LockSupport.park();
                }
            }
            System.out.println("t2 end.");
        }, "t2");
        t1.start();
        t2.start();
    }
}
