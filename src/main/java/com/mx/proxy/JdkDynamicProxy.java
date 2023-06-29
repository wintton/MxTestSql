package com.mx.proxy;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkDynamicProxy implements InvocationHandler {

    //被代理对象
    private Object obj;//一个参数的构造方法，传入被代理对象

    public JdkDynamicProxy(Object obj){
        this.obj=obj;
    }

    /**
     *
     * @return代理类对象。它与被代理对象实现同样的接口
     */
    public Object getProxy(){

        //得到被代理对象的类加载器

        ClassLoader classLoader =  this.obj.getClass().getClassLoader();

        //得到被代理对象实现的接口列表
        Class<?>[] interfaces = this.obj.getClass().getInterfaces();

        Object o = Proxy.newProxyInstance(
                classLoader,
                interfaces,
                this);

        return o;

    }

    public Object getProxyEnhancer(){

        Enhancer enhancer=new Enhancer();

        enhancer.setSuperclass(this.obj.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                begin();

                Object result = method.invoke(obj,objects);

                last();

                System.out.println(method.getName());
                return result;
            }
        });

        Object proxy=enhancer.create();
        return proxy;

    }

    @Override
    public Object invoke(Object proxy,Method method,Object[] args) throws Throwable {

        begin();

        Object result=method.invoke(obj,args);

        last();

        System.out.println(method.getName());

        return result;
    }

    private void begin(){
        System.out.println("进来代理了");
    }

    private void last(){
        System.out.println("我写日志了");
    }

}