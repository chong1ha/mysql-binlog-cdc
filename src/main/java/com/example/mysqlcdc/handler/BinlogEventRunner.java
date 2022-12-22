package com.example.mysqlcdc.handler;

import com.example.mysqlcdc.config.BinlogConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오후 1:33
 */
@Log4j2
@Component
public class BinlogEventRunner implements ApplicationRunner {

    @Autowired
    BinlogConfiguration binlogConfiguration;

    @Autowired
    private BinlogEventHandler binlogEventHandler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("## START Application RUNNER");
        binlogEventHandler.start();
    }

}
