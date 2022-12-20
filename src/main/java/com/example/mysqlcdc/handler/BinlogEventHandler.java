package com.example.mysqlcdc.handler;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.QueryEventData;
import com.github.shyiko.mysql.binlog.event.RowsQueryEventData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오전 10:30`12
 */
@Component("eventListener")
public class BinlogEventHandler implements BinaryLogClient.EventListener {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    @Autowired
    BinaryLogClient binaryLogClient;

    @Override
    public void onEvent(Event event) {
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "## Received " + event);
        }

        if (event.getHeader().getEventType() == EventType.QUERY) {
            QueryEventData data = event.getData();
            System.out.println("[CDC] " + data.toString());
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
