package com.mx.event;

import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

public class TestEvent<T> implements EventData {

    T t;

    public TestEvent(T t){
        this.t = t;
    }

    @Test
    public void doTest(){

    }

    public static void main(String[] args) {
        EventListenerManager eventListenerManager = EventListenerManager.getInstance();

        EventListenerManager.EventRunnable runnable1 = new EventListenerManager.EventRunnable<EventData>() {
            @Override
            public void run(EventData data) {
                if (data != null) System.out.println("1:" + data.getData().toString());
            }
        };
        EventListenerManager.EventRunnable runnable2 = new EventListenerManager.EventRunnable<EventData>() {
            @Override
            public void run(EventData data) {
                if (data != null) System.out.println("1:" + data.getData().toString());
            }
        };
        eventListenerManager.addEventListener("test",runnable1); //添加监听
        eventListenerManager.addEventListener("testjson",runnable2); //添加监听
        JSONObject data = new JSONObject();
        data.put("ceshi","你好啊");
        eventListenerManager.emit("test",new TestEvent("ssssss")); //提交事件
        eventListenerManager.emit("testjson",new TestEvent(data)); //提交事件
    }

    @Override
    public Object getData() {
        return t;
    }
}
