package com.licenta.healthapp.controller;
import com.licenta.healthapp.model.DailyLog;
import com.licenta.healthapp.model.PreferredFoods;
import com.licenta.healthapp.service.DailyLogService;
import com.licenta.healthapp.service.DailyLogServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class DailyLogController {

    @Autowired
    private DailyLogService dailyLogService;

    @Autowired

    public DailyLogController(DailyLogService dailyLogService) {
        this.dailyLogService = dailyLogService;
    }
    @GetMapping("/getLogs")
    public List<DailyLog> getAllLogs() {
        return dailyLogService.getAllLogs();
    }

    @PostMapping("/newLog")
    public ResponseEntity<DailyLog> registerLog(@RequestBody DailyLogRequest logRequest){
        return new ResponseEntity<>(dailyLogService.registerLog(logRequest), HttpStatus.CREATED);
    }

    @PostMapping("/checkAndCreate/{userId}")
    public ResponseEntity<DailyLog> checkAndCreateDailyLog(@PathVariable int userId) {
        try {
            DailyLog dailyLog = dailyLogService.checkAndCreateDailyLog(userId);
            return ResponseEntity.ok(dailyLog);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
