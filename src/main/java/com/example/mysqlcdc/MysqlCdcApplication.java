package com.example.mysqlcdc;

import com.example.mysqlcdc.config.BinlogConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(BinlogConfiguration.class)
@SpringBootApplication
public class MysqlCdcApplication {

	/**
	 * Framework : SpringBoot 2.7.6 (Spring 5.x)<br>
	 * Build Tool : Maven<br>
	 * RDB : Mysql<br>
	 * JAVA 8
	 */
	public static void main(String[] args) {
		SpringApplication.run(MysqlCdcApplication.class, args);
	}

}
