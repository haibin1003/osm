package com.osm.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.osm.**.mapper")
public class MyBatisConfig {
    // MyBatis-Plus auto-configures with Spring Boot 3.x
}
