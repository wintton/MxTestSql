package com.mx.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InvocationHandlerImpl implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("运行 成功了");
        return null;
    }

    public Object getProxy(Object obj){

        ClassLoader classLoader = obj.getClass().getClassLoader();

        Class<?>[] interfaces = obj.getClass().getInterfaces();

        Object o = Proxy.newProxyInstance(classLoader,interfaces,new InvocationHandlerImpl());

        return o;
    }

}
