package com.mx.mxmq;

public interface MQHandler<Message> {
    void hand(Message t);
}
