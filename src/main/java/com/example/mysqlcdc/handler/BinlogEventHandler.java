package com.example.mysqlcdc.handler;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

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

        /* DML Event Process 확인
        *
        * [INSERT] GTID => QUERY => TABLE_MAP => EXT_WRITE_ROWS => XID
        * [UPDATE]
        * [DELETE]
        *  */
        if (log.isInfoEnabled()) {
            log.info("## Received " + event);
        }

        /* QUERY Event 확인 */
        if (event.getHeader().getEventType() == EventType.QUERY) {
            QueryEventData data = event.getData();
            log.info("{0} " + data.toString());
        }

        if (event.getHeader().getEventType() == EventType.ROWS_QUERY) {
            RowsQueryEventData data = event.getData();
            log.info("{1} " + data.getQuery().toString());
        }

        /* 들어온 INSET SQL 확인 */
        if (event.getHeader().getEventType() == EventType.EXT_WRITE_ROWS ) {

            WriteRowsEventData data = event.getData();
            Serializable[] s_data = data.getRows().get(0);
            log.info("{2} " + Arrays.toString(Arrays.stream(s_data).toArray()));
        } else if (event.getHeader().getEventType() == EventType.EXT_UPDATE_ROWS) {

            UpdateRowsEventData data = event.getData();

        } else if (event.getHeader().getEventType() == EventType.EXT_DELETE_ROWS) {
            DeleteRowsEventData data = event.getData();
            Serializable[] s_data = data.getRows().get(0);
            log.info("{4} " + Arrays.toString(Arrays.stream(s_data).toArray()));
        }
    }

    /* (보류) */
    static Map<String, String> getProductMap(Object[] product) {
        Map<String, String> map = new HashMap<>();
        map.put("id", java.lang.String.valueOf(product[0]));
        map.put("name", java.lang.String.valueOf(product[1]));
        map.put("price", java.lang.String.valueOf(product[2]));

        return map;
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
