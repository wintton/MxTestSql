package com.mx.mxmq;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MxMQ<T> {

    public static final int STATE_WAIT = 0;
    public static final int STATE_REMOVE = 1;

    private MxMQ(){
        executors = Executors.newCachedThreadPool();
        partionRunMap = new ConcurrentHashMap<>();
    }

    public static MxMQ getInstance() {
        if(instance == null){
            synchronized (MxMQ.class){
                if(instance == null){
                    instance = new MxMQ();
                }
            }
        }
        return instance;
    }

    private static volatile MxMQ instance = null;

    private ConcurrentHashMap<String,MxMQRunnable<T>> partionRunMap = null;

    private ExecutorService executors =  null;

    /**
     * 添加分区
     * @param partion 分区
     * @param mxHandler 处理器
     * @return
     */
    public boolean addPartion(String partion,MQHandler<T> mxHandler){
        if(partionRunMap.get(partion) == null){
            MxMQRunnable<T> curMxMQRunnable = new MxMQRunnable<T>(mxHandler);
            partionRunMap.put(partion,curMxMQRunnable);
            executors.execute(curMxMQRunnable);
            System.out.println(partion+"被添加");
            return true;
        }
        return false;
    }

    /**
     * 当分区里面没有任务超过20秒后就会自动移除分区
     * @param partion 分区
     * @param mxHandler 处理器
     * @return
     */
    public boolean addPartionAutoRemove(String partion,MQHandler<T> mxHandler){
        if(partionRunMap.get(partion) == null){
            MxMQRunnable<T> curMxMQRunnable = new MxMQRunnable<T>(mxHandler);
            curMxMQRunnable.setState(STATE_REMOVE);
            curMxMQRunnable.setQueueEmpty(new QueueEmpty() {
                @Override
                public void empty(MxMQRunnable mxMQRunnable) {
                    removePartion(partion);
                }
            });
            partionRunMap.put(partion,curMxMQRunnable);
            executors.execute(curMxMQRunnable);
            System.out.println(partion+"被添加");
            return true;
        }
        return false;
    }

    public boolean removePartion(String partion){
        if(partionRunMap.get(partion) != null){
            MxMQRunnable<T> remove = partionRunMap.remove(partion);
            remove.close();
            System.out.println(partion+"被移除");
            return true;
        }
        return false;
    }

    public boolean sendMessage(String partion,T t) throws InterruptedException {
        MxMQRunnable<T> tMxMQRunnable = partionRunMap.get(partion);
        if(tMxMQRunnable != null){
            tMxMQRunnable.sendMessage(t);
            return true;
        }
        return false;
    }

    public boolean removeMessage(String partion,T t){
        MxMQRunnable<T> tMxMQRunnable = partionRunMap.get(partion);
        if(tMxMQRunnable != null){
            return tMxMQRunnable.removeMessage(t);
        }
        return false;
    }

    interface QueueEmpty{
        void empty(MxMQRunnable mxMQRunnable);
    }

}
