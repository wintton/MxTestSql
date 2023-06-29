package com.mx.queue;

import java.util.HashMap;
import java.util.concurrent.*;

public class TestQueue {

    public static void main(String[] args) {

        ArrayBlockingQueue queue = new ArrayBlockingQueue(500); //有界队列

        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(); //有界队列

        SynchronousQueue synchronousQueue = new SynchronousQueue();//有界队列

        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();

        PriorityBlockingQueue priorityBlockingQueue = new PriorityBlockingQueue();

        DelayQueue<MyTask> delayQueue = new DelayQueue<>();

    }


    static class MyTask implements Delayed {

        String name;
        long runningTime;

        MyTask(String name,long rt) {
            this.name = name;
            this.runningTime= rt;
        }
        @Override
        public int compareTo(Delayed other) {
            long td = this.getDelay(TimeUnit.MILLISECONDS);
            long od = other.getDelay(TimeUnit.MILLISECONDS);
            return Long.compare(td,od);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(runningTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public String toString() {
            return name + "-" + runningTime;
        }
    }
}
