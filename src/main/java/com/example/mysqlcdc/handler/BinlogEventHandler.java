package com.example.mysqlcdc.handler;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.RowsQueryEventData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.logging.Level;

/**
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오전 10:30`12
 */
@Log4j2
@Component("eventListener")
public class BinlogEventHandler implements BinaryLogClient.EventListener {

    @Autowired
    BinaryLogClient binaryLogClient;

    @Override
    public void onEvent(Event event) {
        if (log.isInfoEnabled()) {
            log.info("## Received " + event);
        }

        if (event.getHeader().getEventType() == EventType.QUERY) {
            QueryEventData data = event.getData();
            log.info(data.toString());
        }
    }

    @Async
    public Future<String> start() {
        try {
            binaryLogClient.setServerId(binaryLogClient.getServerId() - 1);
            binaryLogClient.setKeepAlive(false);
            binaryLogClient.registerEventListener(this);
            binaryLogClient.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new AsyncResult<String>("## START");
    }

    @Async
    public Future<String> stop() {
        try {
            binaryLogClient.unregisterEventListener(this);
            binaryLogClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AsyncResult<String>("## STOP");
    }
}
