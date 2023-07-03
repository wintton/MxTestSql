package com.mx.limit;

public class LimitRequestUtil {

    private int curCount;
    private int maxCount;
    private long waitTime;

    public final static int NO_WAIT = -1; //不等待
    public final static int ALL_WAIT = 0; // 一直等待

    /**
     * 默认为不等待
     * @param maxCount 最多同时执行最大数
     */
    public LimitRequestUtil(int maxCount){
        this.maxCount = maxCount;
        this.waitTime = NO_WAIT;

    }

    /**
     *
     * @param maxCount 最多同时执行最大数
     * @param waitTime 最长等待时间 ms
     */
    public LimitRequestUtil(int maxCount,long waitTime){
        this.maxCount = maxCount;
        this.waitTime = waitTime;
    }

    private boolean  add(){
        if(curCount >= maxCount){
            return false;
        }
        curCount++;
        return true;
    }

    /**
     * 添加执行数
     * @return 添加成否与否
     */
    public synchronized boolean addCount(){
        if(curCount >= maxCount) {
            if(waitTime == NO_WAIT){
                return false;
            }
            try {
                if (waitTime == ALL_WAIT) {
                    wait();
                } else {
                    wait(waitTime);
                }
            } catch (InterruptedException e) {
                return false;
            }
            return add();
        }
        return add();
    }

    /**
     * 减少执行数
     * @return 减少成功与否
     */
    public synchronized boolean reduceCount(){
        if(curCount <= 0){
            return false;
        }
        curCount--;
        notifyAll();
        return true;
    }


}
