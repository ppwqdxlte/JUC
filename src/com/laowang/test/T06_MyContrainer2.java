package com.laowang.test;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class T06_MyContrainer2<T> {

    private final LinkedList<T> list = new LinkedList<>();
    private final int MAX = 10;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition producer = lock.newCondition();
    private final Condition consumer = lock.newCondition();

    public void add(T t) {
        try {
            lock.lock();//上锁
            while (list.size() == MAX){
                producer.await(3,TimeUnit.SECONDS);//生产者阻塞
            }
            list.add(t);
            System.out.println(Thread.currentThread().getName()+" 生成一个，共有商品 "+ list.size());
            consumer.signalAll();//通知消费者
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();//开锁
        }
    }

    public void get() {
        try {
            lock.lock();
            while (list.size() == 0){
                consumer.await(3, TimeUnit.SECONDS);//阻塞消费者
            }
            T t = list.pop();
            System.out.println(Thread.currentThread().getName()+" 消费一个，还剩商品 "+ list.size());
            producer.signalAll();//通知所有生产者快点生产
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        T06_MyContrainer2<Object> test = new T06_MyContrainer2<>();

        for (int i = 0; i < 2; i++) {
            new Thread(()->{
                for (int j = 0; j < 20; j++) {//每个producer最多生产20个
                    test.add(new Object());
                }
            },"producer"+i).start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                for (int j = 0; j < 5; j++) {//每个consumer最多消费5个
                    test.get();
                }
            },"consumer"+i).start();
        }
    }
}
