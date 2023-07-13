package com.mx.mxmq;


import org.junit.jupiter.api.Test;

public class TestOptional {
    @Test
    public void doTestOptional(){

        MxMQ<Message> mxMQ = MxMQ.getInstance();

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
                System.out.println(message.getMessage());
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
                System.out.println(message.getMessage());
            }
        });

        for(int index = 0;index < 20;index++){
            int finalIndex = index;
            Message message = new Message("test_" + finalIndex);
            Message message2 = new Message("test2_" + finalIndex);
            try {
                mxMQ.sendMessage("test",message);
                mxMQ.sendMessage("test2",message2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (true){}

    }
}
