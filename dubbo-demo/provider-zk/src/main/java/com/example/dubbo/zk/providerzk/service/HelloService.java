package com.example.dubbo.zk.providerzk.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.demo.dubbo.api.Hello;
import org.springframework.beans.factory.annotation.Value;

@Service(timeout = 5000, version = "0.1.0")
public class HelloService implements Hello {
    @Value("${server.port}")
    String port;
    @Override
    public String hello(String name) {
        return "hello " + name + " (" + port + ")";
    }
}
