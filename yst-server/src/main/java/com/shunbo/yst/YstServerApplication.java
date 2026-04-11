package com.shunbo.yst;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.shunbo.yst.modules", markerInterface = BaseMapper.class)
public class YstServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(YstServerApplication.class, args);
    }
}
