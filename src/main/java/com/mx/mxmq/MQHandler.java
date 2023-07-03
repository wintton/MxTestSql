package com.mx.mxmq;

public interface MQHandler<T> {
    void hand(T t);
}
