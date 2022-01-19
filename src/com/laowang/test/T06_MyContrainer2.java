package com.laowang.test;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class T06_MyContrainer2<T> {

    private final LinkedList<T> list = new LinkedList<>();
    private final int MAX = 10;
    private int count;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition producer = lock.newCondition();
    private final Condition consumer = lock.newCondition();

    public void add(T t){
        try {
            lock.lock();
            while (count == MAX){
                producer.await();
            }
            ++count;
            list.add(t);
            System.out.println(Thread.currentThread().getName()+" 生成一个，共有商品 "+ count);
            consumer.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void get(){
        try {
            lock.lock();
            while (count == 0){
                consumer.await();
            }
            --count;
            T t = list.pop();
            System.out.println(Thread.currentThread().getName()+" 消费一个，还剩商品 "+ count);
            producer.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        T06_MyContrainer2 test = new T06_MyContrainer2();

        for (int i = 0; i < 2; i++) {
            new Thread(()->{
                while (true){
                    test.add(new Object());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },"producer"+i).start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                while (true){
                    test.get();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },"consumer"+i).start();
        }
    }
}
