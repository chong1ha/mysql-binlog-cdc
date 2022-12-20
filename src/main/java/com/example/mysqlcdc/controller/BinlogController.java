package com.example.mysqlcdc.controller;

import com.example.mysqlcdc.handler.BinlogEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

/** (보류)
 * @author gunha
 * @version 0.1
 * @since 2022-12-19 오후 2:06
 */
@RestController
public class BinlogController {

    @Autowired
    private BinlogEventHandler binlogEventHandler;

    @RequestMapping("/start")
    public String jobStart(){
        System.out.println("job start ==========>");
        Future<String> rest = binlogEventHandler.start();

        System.out.println(rest.isDone());
        return "JobStartOk";
    }

    @RequestMapping("/stop")
    public String jobStop(){
        System.out.println("job stop ==========>");

        Future<String> rest = binlogEventHandler.stop();

        System.out.println(rest.isDone());
        System.out.println("job end ==========>");
        return "JobStopOk";
    }
}
