package com.mx.mxmq;


import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;


public class TestOptional implements  Message{

    Object t;

    public  TestOptional(Object t){
        this.t = t;
    }

    @Test
    public void doTestOptional(){


        MxMQ mxMQ = MxMQ.getInstance();

        mxMQ.addPartion("testjson", new MQHandler<Message>() {
            @Override
            public void hand(Message message) {

            }
        });

        /**
         * 添加分区 无消息一直阻塞
         */
        mxMQ.addPartion("test", new MQHandler<Message>() {
            @Override
            public void hand(Message message) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(message.getData());
            }
        });

        /**
         * 添加分区 无消息且等待时长超过20秒自动移除该分区
         */
        mxMQ.addPartionAutoRemove("test2", new MQHandler<Message>() {
            @Override
            public void hand(Message message) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(message.getData());
            }
        });

        for(int index = 0;index < 20;index++){
            int finalIndex = index;
            Message message = new Message(){

                @Override
                public Object getData() {
                    return null;
                }
            };
            Message message2 = new TestOptional("test2_" + finalIndex);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("index",index);
            try {
                mxMQ.sendMessage("test",message);
                mxMQ.sendMessage("test2",message2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (true){}

    }

    @Override
    public Object getData() {
        return t;
    }
}
