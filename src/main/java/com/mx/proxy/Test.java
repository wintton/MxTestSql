package com.mx.proxy;


public class Test {

    public static void main(String[] args) {
        JdkDynamicProxy proxy = new JdkDynamicProxy(new ProductDaoImpl());
        IGeneralDao proxy1 = (IGeneralDao)proxy.getProxy();
        proxy1.update();
    }

}
