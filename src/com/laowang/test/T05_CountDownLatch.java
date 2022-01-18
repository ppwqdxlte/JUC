package com.laowang.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class T05_CountDownLatch {

    private List<Object> list = new ArrayList<>();

    public void add(Object o){
        list.add(o);
    }
    public int size(){
        return list.size();
    }

    public static void main(String[] args) {
        T05_CountDownLatch test = new T05_CountDownLatch();

        CountDownLatch latch = new CountDownLatch(1);//门闩类，形参相当于钥匙盒，许可池，被门闩控制的线程需要拿到许可才能运行，许可拿走一个，盒子就少一个，正所谓count down

        new Thread(()->{
            System.out.println("t1开始运行");
            try {
                latch.await();//给当前线程上门闩
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t1结束");
            latch.countDown();//帮t2开门闩
        },"t1").start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(()->{
            synchronized (latch){
                System.out.println("t2开始运行");
                for (int i = 0; i < 10; i++) {
                    test.add(new Object());
                    System.out.println("add "+ i);
                /*
                若是不让t2每次歇一会儿，t1就不会在5后紧跟着打印，而是6甚至以后，t1反应不过来，那么怎么能准确呢？
                bingo!!!::用两个门闩！！先闩t1，到点儿后，再闩t2，然后在t2线程内，上门闩锁住t2，就能正确顺序运行了！
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                    if (test.size() == 5){
                        latch.countDown();//再给t1开门栓
                        try {
                            latch.await();//先给自己上门闩
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            System.out.println("t2结束");
        },"t2").start();

    }
}
