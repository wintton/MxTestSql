package com.mx.proxy;

public class ProductDaoImpl implements IGeneralDao{

    @Override
    public void insert() {
        System.out.println("插入");
    }

    @Override
    public void update() {
        System.out.println("更新");
    }

    @Override
    public void delete() {
        System.out.println("删除");
    }

    @Override
    public void update(int number) {
        System.out.println("删除");
    }
}
