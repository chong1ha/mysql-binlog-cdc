package com.example.mysqlcdc;

import com.example.mysqlcdc.config.BinlogConfiguration;
import com.example.mysqlcdc.handler.BinlogEventHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(BinlogConfiguration.class)
@SpringBootApplication(
		exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
		scanBasePackageClasses = {BinlogEventHandler.class})
public class MysqlCdcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysqlCdcApplication.class, args);
	}

}
