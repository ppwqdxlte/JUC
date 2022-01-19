package com.laowang.test;

import java.util.LinkedList;

/*
* 【题目】写一个容器，生产者消费者模式，用wait()或notify()或notifyAll()实现 2个生产者和若干个消费者
* */
public class T05_MyContainer1<T> {

    private LinkedList<T> list = new LinkedList<>();

    private final int MAX = 10;

    private int count;

    public synchronized void add(T t){
        if (count < MAX){
            list.push(t);
            ++count;
            System.out.println(Thread.currentThread().getName()+" 制造了一个，目前库存为"+count);
            this.notifyAll();
        }else{
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized T get(){
        if (count == 0){
            try {
                this.notify();//通知厂家赶紧造
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            --count;
            System.out.println(Thread.currentThread().getName()+"取走了一个，目前库存为"+count);
        }
        return list.poll();
    }

    public static void main(String[] args) {
        T05_MyContainer1<Object> container1 = new T05_MyContainer1<>();

        for (int i = 0; i < 2; i++) {
            new Thread(()->{
                while (true){
                    container1.add(new Object());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },"producer"+i).start();
        }

        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                while (true){
                    container1.get();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },"consumer"+i).start();
        }
    }

}
