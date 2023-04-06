package com.example.mysqlcdc;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * MySQL 변경 데이터 캡처 (streaming to java)
 *
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오후 1:33
 */
@Log4j2
@Component
public class BinlogEventRunner implements ApplicationRunner, BinaryLogClient.EventListener {

    @Autowired
    private BinaryLogClient binaryLogClient;

    public BinlogEventRunner(BinaryLogClient binaryLogClient) {
        this.binaryLogClient = binaryLogClient;
    }

    /** 발생한 변경(이벤트) 데이터의 테이블명 */
    private String dbTableName = "";
    /** 발생한 변경(이벤트) 데이터의 컬럼 명 (n개)  */
    private Map<String, List<String>> dbTableColumnNames = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // BinaryLogClient 생성 및 이벤트리스너로 등록
            setBinlogClient();
        } catch (Exception e) {
            log.error(e);
            // 예외 시, 종료
            exitBinlogClient();
        }
    }

    /**
     * 발생하는 이벤트 처리
     *
     * @param event 발생한 변경(이벤트) 데이터
     */
    @Override
    public void onEvent(Event event) {
        /*
         * DML Event Process
         *  - 변경 이벤트에 대해서 5개의 Event 가 순차적으로 들어옴
         *    -> 1 row changed ( GTID => QUERY => TABLE_MAP => EXT_WRITE_ROWS => XID ) Event
         */

        // DML의 Event Process 에 맞게 Event 가 들어있는지 확인
        if (log.isInfoEnabled()) {
            log.info("## Received Total = {} ", event);
        }

        /*
         * TableMap 처리
         *  -> 변경이벤트의 (스키마명, 테이블명, 컬럼명) 얻기
         */
        if (event.getHeader().getEventType() == EventType.TABLE_MAP) {
            // 변경이벤트의 스키마명과 테이블명 얻기
            TableMapEventData tableMapEventData = event.getData();
            dbTableName = tableMapEventData.getTable();
            log.info("## DB Schema = {}, Table = {}", tableMapEventData.getDatabase(), dbTableName);

            // 변경 데이터의 컬럼명
            List<String> columnNames = new ArrayList<>();

            for (String column : tableMapEventData.getEventMetadata().getColumnNames()) {
                columnNames.add(column);
            }
            dbTableColumnNames.put(dbTableName, columnNames);
        }

        /* 들어온 INSERT SQL 확인 */
        if (event.getHeader().getEventType() == EventType.EXT_WRITE_ROWS ) {
            WriteRowsEventData data = event.getData();

            List<String> columnNames = dbTableColumnNames.get(dbTableName);
            List<Map<String, String>> rowDataList= new ArrayList<>();

            // 변경 데이터의 컬럼과 데이터 추출
            for (int i = 0; i < data.getRows().size(); i++) {

                Map<String, String> rowData = new HashMap<>();

                for (int j = 0; j < columnNames.size(); j++) {

                    Serializable value = data.getRows().get(i)[j];
                    rowData.put(columnNames.get(j), value.toString());
                }
                rowDataList.add(rowData);
            }
            log.info("## Insert event: {}", rowDataList);
        }
        /* 들어온 UPDATE SQL 확인 */
        else if (event.getHeader().getEventType() == EventType.EXT_UPDATE_ROWS) {
            UpdateRowsEventData data = event.getData();

            List<String> columnNames = dbTableColumnNames.get(dbTableName);
            List<Map<String, String>> rowBeforeDataList= new ArrayList<>();
            List<Map<String, String>> rowAfterDataList= new ArrayList<>();

            // 변경 데이터의 컬럼과 데이터 추출
            for (int i = 0; i < data.getRows().size(); i++) {

                Map<String, String> rowBeforeData = new HashMap<>();
                Map<String, String> rowAfterData = new HashMap<>();

                for (int j = 0; j < columnNames.size(); j++) {

                    Serializable before = data.getRows().get(i).getValue()[j];
                    Serializable after = data.getRows().get(i).getValue()[j];
                    rowBeforeData.put(columnNames.get(j), before.toString());
                    rowAfterData.put(columnNames.get(j), after.toString());
                }
                rowBeforeDataList.add(rowBeforeData);
                rowAfterDataList.add(rowAfterData);
            }
            log.info("## DELETE event: before = {}, after = {}", rowBeforeDataList, rowAfterDataList);
        }
        /* 들어온 DELETE SQL 확인 */
        else if (event.getHeader().getEventType() == EventType.EXT_DELETE_ROWS) {
            DeleteRowsEventData data = event.getData();

            List<String> columnNames = dbTableColumnNames.get(dbTableName);
            List<Map<String, String>> rowDataList= new ArrayList<>();

            // 변경 데이터의 컬럼과 데이터 추출
            for (int i = 0; i < data.getRows().size(); i++) {
                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < columnNames.size(); j++) {
                    Serializable value = data.getRows().get(i)[j];
                    rowData.put(columnNames.get(j), value.toString());
                }
                rowDataList.add(rowData);
            }
            log.info("## DELETE event: {}", rowDataList);
        }
    }

    public void setBinlogClient() throws IOException, TimeoutException {
        // BinaryLogClient 인스턴스 간 충돌 방지
        binaryLogClient.setServerId(binaryLogClient.getServerId() - 1);
        // TCP 연결 유지 여부, 일시적인 연결만 필요하므로 false
        binaryLogClient.setKeepAlive(false);
        binaryLogClient.registerEventListener(this);
        // 5초 동안 연결이 이루어지지 않으면, 예외 발생
        binaryLogClient.connect(5000);
    }

    public void exitBinlogClient() throws IOException {
        try {
            binaryLogClient.unregisterEventListener(this);
        } finally {
            binaryLogClient.disconnect();
        }
    }
}
