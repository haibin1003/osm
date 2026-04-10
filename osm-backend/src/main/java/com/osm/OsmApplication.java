package com.osm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

@SpringBootApplication(exclude = {
    DataSourceTransactionManagerAutoConfiguration.class
})
@MapperScan("com.osm.**.mapper")
public class OsmApplication {
    public static void main(String[] args) {
        SpringApplication.run(OsmApplication.class, args);
    }
}
