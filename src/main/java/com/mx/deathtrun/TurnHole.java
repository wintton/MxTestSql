package com.mx.deathtrun;

public interface TurnHole {
    boolean loadBullet();
    boolean shoot();
    void setNext(TurnHole turnHole);
    TurnHole next();
    boolean clear();
    boolean isBullet();
}
