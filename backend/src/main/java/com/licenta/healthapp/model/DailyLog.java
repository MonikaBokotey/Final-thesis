package com.licenta.healthapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "DailyLogs")
public class DailyLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int daily_log_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date_time")
    private LocalDateTime logDateTime;

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Meal> meals;

    public DailyLog() {
    }

    public int getDaily_log_id() {
        return daily_log_id;
    }

    public void setDaily_log_id(int daily_log_id) {
        this.daily_log_id = daily_log_id;
    }

    public DailyLog(User user, LocalDateTime logDateTime) {
        this.user = user;
        this.logDateTime = logDateTime;
    }

    public LocalDateTime getLogDateTime() {
        return logDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLogDateTime(LocalDateTime logDateTime) {
        this.logDateTime = logDateTime;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }


}