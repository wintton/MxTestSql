package com.example.dubbo.zk.providerzk;


import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ProviderZkApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProviderZkApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
