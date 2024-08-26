package com.licenta.healthapp.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "Workouts")
public class Workout implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int workout_id;

    @ManyToOne
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    @Column(name = "name")
    private String name;

    @Column(name = "reps")
    private int reps;

    @Column(name = "duration")
    private int duration;

    @Column(name = "intensity")
    private String intensity;

    @Column(name = "workout_time")
    private LocalDateTime workoutTime;

    public Workout() {
    }

    public int getWorkout_id() {
        return workout_id;
    }

    public void setWorkout_id(int workout_id) {
        this.workout_id = workout_id;
    }

    public DailyLog getDailyLog() {
        return dailyLog;
    }

    public void setDailyLog(DailyLog dailyLog) {
        this.dailyLog = dailyLog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public LocalDateTime getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(LocalDateTime workoutTime) {
        this.workoutTime = workoutTime;
    }
}
