package com.licenta.healthapp.controller;

import com.licenta.healthapp.model.User;

import java.time.LocalDateTime;

public class DailyLogRequest {
    private int dailyLogId;
    private LocalDateTime logDateTime;
    private User user;

    public DailyLogRequest(int dailyLogId, LocalDateTime logDateTime, User user) {
        this.dailyLogId = dailyLogId;
        this.logDateTime = logDateTime;
        this.user = user;
    }

    public int getDailyLogId() {
        return dailyLogId;
    }

    public void setDailyLogId(int dailyLogId) {
        this.dailyLogId = dailyLogId;
    }

    public LocalDateTime getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(LocalDateTime logDateTime) {
        this.logDateTime = logDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
