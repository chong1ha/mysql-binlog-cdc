package com.example.mysqlcdc;

import com.example.mysqlcdc.config.BinlogConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(BinlogConfiguration.class)
@SpringBootApplication
public class MysqlCdcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysqlCdcApplication.class, args);
	}

}
