package com.example.mysqlcdc.handler;

import com.example.mysqlcdc.config.BinlogConfiguration;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.ByteArrayEventDataDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.NullEventDataDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

/**
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오후 1:33
 */
@Component
public class BinlogEventRunner implements ApplicationRunner {

    @Autowired
    BinlogConfiguration binlogConfiguration;

    @Autowired
    private BinlogEventHandler binlogEventHandler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("## START Application RUNNER");
        binlogEventHandler.start();
    }

}
