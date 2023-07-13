package com.mx.deathtrun;

import javax.crypto.Mac;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GunTurnTable implements TurnTable{

    List<TurnHole> turnTable;   //转孔集合
    int curIndex = 0;      //当前游标

    public  GunTurnTable(int number){
        turnTable = new ArrayList<>();
        curIndex = 0;
        for (int i = 0; i < number; i++) {
            turnTable.add(new GunTurnHole());
        }
    }

    /**
     * 射击
     * @return
     */
    @Override
    public boolean shoot() {
        curIndex++;
        if(Math.random() > 0.55 && Math.random() < 0.57){
            PlayGame.printer.print("孔位" + curIndex + " 射击=========>");
            PlayGame.printer.print("卡壳");
            curIndex--;
            return false;
        }
        if(curIndex >= turnTable.size()){
            curIndex = 0;
        }
        PlayGame.printer.print("孔位" + curIndex + " 射击=========>");
        return turnTable.get(curIndex).shoot();
    }

    /**
     * 旋转转盘 实际随机游标
     * @return
     */
    @Override
    public boolean random() {
        curIndex = (int)Math.ceil(Math.random() * turnTable.size());
        PlayGame.printer.print("轮盘旋转==========>");
        return true;
    }

    /**
     * 装子弹
     * @param number 数量
     * @return
     */
    @Override
    public boolean loadBullet(int number) {
        for (int i = 0; i < number; i++) {
            curIndex++;
            if(curIndex >= turnTable.size()){
                curIndex = 0;
            }
            turnTable.get(curIndex).loadBullet();
            PlayGame.printer.print("孔位" + curIndex + " 上子弹======>");
        }
        return true;
    }

    /**
     * 随机装子弹
     * @param number 子弹数量 不一定实际装单数
     * @param prec 百分比先随机
     * @return
     */
    @Override
    public boolean loadBulletRandom(int number,int prec) {
        for (int i = 0; i < number; i++) {
            if(curIndex >= turnTable.size()){
                curIndex = 0;
            }
            if(Math.random() <= prec * 0.01f){
                turnTable.get(curIndex).loadBullet();
                PlayGame.printer.print("孔位" + curIndex + " 上子弹======>");
            }
        }
        return true;
    }

    /**
     * 所有孔位下子弹
     * @return
     */
    @Override
    public boolean clear() {
        int index = 0;
        for (TurnHole turnHole : turnTable) {
            if(turnHole.isBullet()){
                turnHole.clear();
                PlayGame.printer.print("孔位" + index + " 下子弹======>");
            }

            index++;
        }
        return true;
    }

    @Override
    public int getHoleSize() {
        if(turnTable == null){
           return 0;
        }
        return turnTable.size();
    }
}
