package com.laowang.concurrencyWithNoVolatile;

import java.util.ArrayList;
import java.util.List;

public class NoVolatile {

    private List<Object> list = new ArrayList<>();

    public void add(Object o){
        this.list.add(o);
    }

    public int size(){
        return list.size();
    }

    public static void main(String[] args) {

        NoVolatile nv = new NoVolatile();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                nv.add(new Object());
            }
            System.out.println(Thread.currentThread().getName()+"--------------"+nv.size());
        },"t1").start();

        new Thread(()->{
            while (true){
                if (nv.size()==5){
                    System.out.println(Thread.currentThread().getName()+"--------------"+nv.size());
                    break;
                }
            }
        },"t2").start();
    }
}
