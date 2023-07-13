package com.mx.leetcode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Foo {

    Semaphore semaphore = new Semaphore(0);
    Semaphore semaphore1 = new Semaphore(0);

    public Foo(){

    }

    public void first(Runnable printFirst) throws  InterruptedException{
        printFirst.run();
        semaphore.release();
    }

    public void second(Runnable printSecond) throws  InterruptedException{
        semaphore.acquire();
        printSecond.run();
        semaphore1.release();
    }

    public void third(Runnable printThird) throws  InterruptedException{
        semaphore1.acquire();
        printThird.run();
    }


    public static void main(String[] args) {
       Foo foo = new Foo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    foo.second(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("second");
                        }
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    foo.third(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("third");
                        }
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   foo.first(new Runnable() {
                       @Override
                       public void run() {
                           System.out.println("first");
                       }
                   });
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
       }).start();
    }
}
