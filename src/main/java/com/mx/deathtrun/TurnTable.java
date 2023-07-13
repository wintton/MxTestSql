package com.mx.deathtrun;

/**
 * 转盘
 */
public interface TurnTable {
    boolean shoot();
    boolean random();
    boolean loadBullet(int number);
    boolean loadBulletRandom(int number,int prec);
    boolean clear();
    int getHoleSize();
}
