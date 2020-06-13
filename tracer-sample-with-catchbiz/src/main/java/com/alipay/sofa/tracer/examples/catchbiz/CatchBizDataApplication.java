package com.alipay.sofa.tracer.examples.catchbiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CatchBizDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatchBizDataApplication.class, args);
    }

}
