package com.licenta.healthapp.service;

import com.licenta.healthapp.controller.DailyLogRequest;
import com.licenta.healthapp.model.DailyLog;
import com.licenta.healthapp.model.User;
import com.licenta.healthapp.repository.DailyLogRepository;
import com.licenta.healthapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
public class DailyLogServiceImplementation implements DailyLogService{

    @Autowired
    private DailyLogRepository dailyLogRepository;
    @Autowired
    private UserRepository userRepository;

    private User user;

    public DailyLogServiceImplementation(DailyLogRepository dailyLogRepository) {
        this.dailyLogRepository = dailyLogRepository;
    }


    @Override
    public List<DailyLog> getAllLogs(){
        return dailyLogRepository.findAll();
    }


    @Override
    public DailyLog registerLog(DailyLogRequest dailyLogRequest){
        DailyLog dailyLog=new DailyLog(user, LocalDateTime.now());

        return dailyLogRepository.save(dailyLog);

    }

    @Override
    public DailyLog checkAndCreateDailyLog(int userId) {
        try {

            LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);


            LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);


            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {

                throw new RuntimeException("User not found with id " + userId);
            }


            DailyLog dailyLog = dailyLogRepository.findByUserAndLogDateTimeBetween(user, startOfDay, endOfDay);


            if (dailyLog == null) {
                dailyLog = new DailyLog();
                dailyLog.setUser(user);
                dailyLog.setLogDateTime(LocalDateTime.now());
                dailyLog = dailyLogRepository.save(dailyLog);
            }

            return dailyLog;
        } catch (Exception e) {

            System.err.println("An error occurred in checkAndCreateDailyLog: " + e.getMessage());
            e.printStackTrace();


            throw e;
        }
    }
}
