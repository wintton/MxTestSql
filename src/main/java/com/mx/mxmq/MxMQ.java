package com.mx.mxmq;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MxMQ {

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

    private ConcurrentHashMap<String,MxMQRunnable<Message>> partionRunMap = null;

    private ExecutorService executors =  null;

    /**
     * 添加分区
     * @param partion 分区
     * @param mxHandler 处理器
     * @return
     */
    public boolean addPartion(String partion,MQHandler<Message> mxHandler){
        if(partionRunMap.get(partion) == null){
            MxMQRunnable<Message> curMxMQRunnable = new MxMQRunnable<Message>(mxHandler);
            partionRunMap.put(partion,curMxMQRunnable);
            executors.execute(curMxMQRunnable);
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
    public boolean addPartionAutoRemove(String partion,MQHandler<Message> mxHandler){
        if(partionRunMap.get(partion) == null){
            MxMQRunnable<Message> curMxMQRunnable = new MxMQRunnable<Message>(mxHandler);
            curMxMQRunnable.setState(STATE_REMOVE);
            curMxMQRunnable.setQueueEmpty(new QueueEmpty() {
                @Override
                public void empty(MxMQRunnable mxMQRunnable) {
                    removePartion(partion);
                }
            });
            partionRunMap.put(partion,curMxMQRunnable);
            executors.execute(curMxMQRunnable);
            return true;
        }
        return false;
    }

    public boolean removePartion(String partion){
        if(partionRunMap.get(partion) != null){
            MxMQRunnable<Message> remove = partionRunMap.remove(partion);
            remove.close();
            return true;
        }
        return false;
    }

    public boolean sendMessage(String partion,Message t) throws InterruptedException {
        MxMQRunnable<Message> tMxMQRunnable = partionRunMap.get(partion);
        if(tMxMQRunnable != null){
            tMxMQRunnable.sendMessage(t);
            return true;
        }
        return false;
    }

    public boolean removeMessage(String partion,Message t){
        MxMQRunnable<Message> tMxMQRunnable = partionRunMap.get(partion);
        if(tMxMQRunnable != null){
            return tMxMQRunnable.removeMessage(t);
        }
        return false;
    }

    interface QueueEmpty{
        void empty(MxMQRunnable mxMQRunnable);
    }

}
