package com.example.ylipi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.ylipi.mapper")
public class YlipiApplication {

    public static void main(String[] args) {
        SpringApplication.run(YlipiApplication.class, args);
    }

}
