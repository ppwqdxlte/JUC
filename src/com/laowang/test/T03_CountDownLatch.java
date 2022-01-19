package com.laowang.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class T03_CountDownLatch {

    private List<Object> list = new ArrayList<>();

    public void add(Object o){
        list.add(o);
    }
    public int size(){
        return list.size();
    }

    public static void main(String[] args) {
        T03_CountDownLatch test = new T03_CountDownLatch();

        CountDownLatch latch = new CountDownLatch(1);//门闩类，形参相当于钥匙盒，许可池，被门闩控制的线程需要拿到许可才能运行，许可拿走一个，盒子就少一个，正所谓count down

        new Thread(()->{
            System.out.println("t1开始运行");
            if (test.size() != 5){
                try {
                    latch.await();//给当前线程上门闩
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("t1结束");
            latch.countDown();//帮t2开门闩
        },"t1").start();

        new Thread(()->{
                System.out.println("t2开始运行");
                for (int i = 0; i < 10; i++) {
                    if (test.size() == 5) {
                        latch.countDown();//再给t1开门栓
                        try {
                            latch.await();//先给自己上门闩
                            test.add(new Object());     //解除阻塞，马上添加第6个元素【add 5】
                            System.out.println("add " + i);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            e.printStackTrace();
                        }
                    }else {     // add 0,1,2,3,4,(i==5已然跳过),6,7,8,9
                        //【注意】！！！如果没有else分支，可能发生指令重排序！add 5会提前打印
                        //也就是说，并发处理的分支，一定要跟正常逻辑分开！！否则顺序会出问题
                        test.add(new Object());
                        System.out.println("add " + i);
                    }
                }
                System.out.println("t2结束");
        },"t2").start();
    }
}
