package com.laowang.test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
/*
* jdk1.9之后才有这个类，本质指向某个对象的引用，实现了很多原子操作，不用重锁,
* VarHandle可以修改成员属性值，反射也能实现这个功能，但是效率要比反射高很多，
* 因为VarHandle不用检验，可以理解成直接操作二进制码
* */
public class T07_VarHandle {

    private int x = 8;

    private static VarHandle handle;

    static {
        try {
            handle = MethodHandles.lookup().findVarHandle(T07_VarHandle.class,"x",int.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        T07_VarHandle t = new T07_VarHandle();

        System.out.println(handle.get(t));
        System.out.println(handle.get(t).getClass());System.out.println();

        handle.set(t,9);
        System.out.println(t.x);System.out.println();

        handle.compareAndSet(t,9,10);
        System.out.println(t.x);System.out.println();

        handle.getAndAdd(t,11);
        System.out.println(t.x);System.out.println();
    }
}
