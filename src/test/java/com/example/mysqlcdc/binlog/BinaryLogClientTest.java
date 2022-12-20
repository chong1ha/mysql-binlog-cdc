package com.example.mysqlcdc.binlog;

import com.example.mysqlcdc.config.BinlogConfiguration;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author gunha
 * @version 0.1
 * @since 2022-12-20 오후 1:32
 */
public class BinaryLogClientTest {

    @Test
    public void testEventListener() {
        //given
        String hostname = "localhost";
        Integer port = 3306;
        String username = "mysql";
        String password = "mysql";

        //when
        BinaryLogClient binaryLogClient = new BinaryLogClient(
                hostname,
                port,
                username,
                password);

        //then
        assertTrue(binaryLogClient.getEventListeners().isEmpty());
    }
}
