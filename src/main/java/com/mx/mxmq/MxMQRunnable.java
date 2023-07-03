package com.mx.mxmq;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MxMQRunnable<T> implements Runnable{

    boolean isRun = false;
    ArrayBlockingQueue<T> arrayBlockingQueue = null;
    MQHandler<T> mqHandler = null;
    int state = 0;

    MxMQ.QueueEmpty queueEmpty = null;

    public void setQueueEmpty(MxMQ.QueueEmpty queueEmpty) {
        this.queueEmpty = queueEmpty;
    }

    public MxMQRunnable(MQHandler<T> mqHandler){
        isRun = true;
        arrayBlockingQueue = new ArrayBlockingQueue(50);
        this.mqHandler = mqHandler;
        state = MxMQ.STATE_WAIT;
    }

    public MxMQRunnable(int number,MQHandler<T> mqHandler){
        arrayBlockingQueue = new ArrayBlockingQueue(number);
        this.mqHandler = mqHandler;
        state = MxMQ.STATE_WAIT;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public void run() {
        while (isRun){
            try {
                T t = null;
                if(state == MxMQ.STATE_WAIT){
                   t = arrayBlockingQueue.take();
                } else {
                   t = arrayBlockingQueue.poll(20,TimeUnit.SECONDS);
                   if(t == null){
                       close();
                       queueEmpty.empty(this);
                       break;
                   }
                }
                if(mqHandler != null){
                    mqHandler.hand(t);
                }
            } catch (Exception e) {
                 e.printStackTrace();
            }
        }
    }

    public boolean sendMessage(T t) throws InterruptedException {
        return arrayBlockingQueue.offer(t,20, TimeUnit.SECONDS);
    }

    public boolean removeMessage(T t){
        return arrayBlockingQueue.remove(t);
    }

    public void close(){
        isRun = false;
    }

}
