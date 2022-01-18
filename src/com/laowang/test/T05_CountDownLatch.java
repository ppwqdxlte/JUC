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
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t1结束");
        },"t1").start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(()->{
            System.out.println("t2开始运行");

            for (int i = 0; i < 10; i++) {
                test.add(new Object());
                System.out.println("add "+ i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (test.size() == 5){
                    latch.countDown();//扔给其它线程一个钥匙，此时钥匙盒没了钥匙了，t2线程自己也没钥匙就暂时阻塞住了，等t1用完钥匙，t2才会重新获得钥匙
                }
            }
            System.out.println("t2结束");
        },"t2").start();

    }
}
