package com.laowang.test;

import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

/*
* 【题目】写一个容器，生产者消费者模式，用wait()或notify()或notifyAll()实现 2个生产者和若干个消费者
* */
public class T05_MyContainer1<T> {

    private LinkedList<T> list = new LinkedList<>();

    private final int MAX = 10;

    private int count;

    public synchronized void add(T t) throws TimeoutException {
        long startUp = System.currentTimeMillis();
        while (count == MAX){ //删掉停顿时间后，if-else有问题，所以改成了while
            if (System.currentTimeMillis()-startUp >= 10000){
                this.notifyAll();//通知consumer，防止阻塞
                throw new TimeoutException("工厂"+Thread.currentThread().getName()+"等待超时，停止生产");
            }
            try {
                this.notifyAll();
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        list.push(t);
        ++count;
        System.out.println(Thread.currentThread().getName()+" 制造了一个，目前库存为"+count);
        this.notifyAll();
    }
    public synchronized T get() throws TimeoutException {
        long startUp = System.currentTimeMillis();
        while (count == 0){
            if (System.currentTimeMillis() - startUp >= 10000){
                this.notifyAll();//通知其它线程尤其producer，防止阻塞
                throw new TimeoutException("消费者"+Thread.currentThread().getName()+"等待超时，取消订单");
            }
            try {
                this.notify();//通知厂家赶紧造
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        --count;
        System.out.println(Thread.currentThread().getName()+"取走了一个，目前库存为"+count);
        return list.poll();
    }

    public static void main(String[] args) {
        T05_MyContainer1<Object> container1 = new T05_MyContainer1<>();

        for (int i = 0; i < 2; i++) {
            new Thread(()->{
                for (int j = 0; j < 25; j++) {
                    try {
                        container1.add(new Object());
                    } catch (TimeoutException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }
            },"producer"+i).start();
        }

        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                for (int j = 0; j < 5; j++) {
                    try {
                        container1.get();
                    } catch (TimeoutException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }
            },"consumer"+i).start();
        }
    }

}
