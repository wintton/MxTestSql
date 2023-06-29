package com.demo.dubbo.zk.consumerzk;


import com.alibaba.dubbo.config.annotation.Reference;
import com.demo.dubbo.api.Hello;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsumerZkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerZkApplication.class, args);
    }

    @Reference(version = "0.1.0")
    private Hello helloService;

    @Bean
    public ApplicationRunner runner() {
        return args -> System.out.println(helloService.hello("dubbo-zk"));
    }

}
