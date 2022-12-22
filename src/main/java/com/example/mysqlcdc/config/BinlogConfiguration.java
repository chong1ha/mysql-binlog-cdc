package com.example.mysqlcdc.config;

import com.example.mysqlcdc.handler.BinlogEventHandler;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오전 10:28
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "binlog")
public class BinlogConfiguration {

    /* BinLog Conn */
    private String host;
    private int port;
    private String user;
    private String password;

    /**
     * Tapping into MySQL Replication Stream
     *   - [전제] Replication Slave Privilege
     */
    @Bean
    BinaryLogClient binaryLogClient(){

        BinaryLogClient binaryLogClient = new BinaryLogClient(
                host,
                port,
                user,
                password);

        /* 받은 데이터를 BYTE 로 표현 */
//        EventDeserializer eventDeserializer = new EventDeserializer();
//        eventDeserializer.setCompatibilityMode(
//                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
//                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
//        );
//        binaryLogClient.setEventDeserializer(eventDeserializer);
        return binaryLogClient;
    }

    @Bean
    public BinaryLogClient.EventListener eventListener(){
        return new BinlogEventHandler();
    }

}
