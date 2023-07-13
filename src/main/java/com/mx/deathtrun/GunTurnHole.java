package com.mx.deathtrun;

/**
 * 转孔 孔里面有无子弹
 */
public class GunTurnHole implements TurnHole{
    boolean bullet = false; //是否有子弹
    TurnHole turnHole = null;   //下一个孔位

    /**
     * 装弹
     * @return
     */
    @Override
    public boolean loadBullet() {
        if(!bullet){
            bullet = true;
            return true;
        }
        return false;
    }
    @Override
    public boolean isBullet() {
        return bullet;
    }

    /**
     * 射击
     * @return
     */
    @Override
    public boolean shoot() {
        if(bullet){
            bullet = false;
            return true;
        }
        return false;
    }

    /**
     * 下一个孔位
     * @param turnHole
     */
    @Override
    public void setNext(TurnHole turnHole) {
        this.turnHole = turnHole;
    }

    /**
     * 返回下一个孔位
     * @return
     */
    @Override
    public TurnHole next() {
        return this.turnHole;
    }

    /**
     * 清子弹
     * @return
     */
    @Override
    public boolean clear() {
        bullet = false;
        return true;
    }
}
