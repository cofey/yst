package com.yst;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yst.mapper")
public class YstServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YstServerApplication.class, args);
    }
}
