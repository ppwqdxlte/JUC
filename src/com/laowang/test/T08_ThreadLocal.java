package com.laowang.test;

public class T08_ThreadLocal {

    private static final ThreadLocal local = new ThreadLocal();

    private static class User{
        private String name;
        public User(String name){
            this.name = name;
        }
        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(local.get());
        },"t1").start();

        new Thread(()->{
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            local.set(new User("虎子"));
            System.out.println(local.get());
        },"t2").start();
    }
}
