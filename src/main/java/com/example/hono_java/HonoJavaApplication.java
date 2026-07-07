package com.example.hono_java;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.hono_java.mapper")
public class HonoJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HonoJavaApplication.class, args);
    }

}
