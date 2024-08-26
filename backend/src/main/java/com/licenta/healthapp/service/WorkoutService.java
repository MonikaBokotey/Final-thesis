package com.licenta.healthapp.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface WorkoutService {
    public void addWorkoutForUserToday(int userId, String name, int reps, int duration, String intensity);
    public void addWorkoutForUserTodayFromCSV(int userId);
    public Map<String, Integer> readExerciseDataFromCsv(String csvFilePath);

}
