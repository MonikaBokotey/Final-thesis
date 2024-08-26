package com.licenta.healthapp.service;

import com.licenta.healthapp.controller.DailyLogRequest;
import com.licenta.healthapp.model.DailyLog;
import com.licenta.healthapp.model.PreferredFoods;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DailyLogService {

    List<DailyLog> getAllLogs();
    DailyLog registerLog(DailyLogRequest dailyLogRequest);

    DailyLog checkAndCreateDailyLog(int userId);
}
