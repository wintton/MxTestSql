package com.mx.deathtrun;

import java.util.HashMap;
import java.util.Map;

public class PlayGame {

    public static Printer printer = new Printer();  //打印器

    private TurnTable turnTable;    //转盘

    Map<String,Handler> handlerMap = new HashMap<>();   //处理器

    public PlayGame(TurnTable turnTable){
        this.turnTable = turnTable;
    }

    public TurnTable getTurnTable() {
        return turnTable;
    }

    public int getHoleSize(){
        return turnTable.getHoleSize();
    }

    /**
     * 添加处理器
     * @param handler
     */
    public void addHandler(Handler handler){
        handlerMap.put(handler.getName(),handler);
    }

    public void hand(String name){
        Handler handler = handlerMap.get(name);
        if(handler == null){
            printer.print("错误的处理器=============>");
            return;
        }
        handler.hand();
    }

    public void start(){
        hand("start");
    }

    public void print(String message){
        printer.print(message);
    }

}
