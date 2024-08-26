package com.licenta.healthapp.service;

import com.licenta.healthapp.model.DailyLog;
import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.Workout;
import com.licenta.healthapp.repository.DailyLogRepository;
import com.licenta.healthapp.repository.UserRepository;
import com.licenta.healthapp.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkoutServiceImplementation implements WorkoutService{
    @Autowired
    private WorkoutRepository workoutsRepository;

    @Autowired
    private DailyLogRepository dailyLogsRepository;

    @Autowired
    private UserRepository usersRepository;

    public void addWorkoutForUserToday(int userId, String name, int reps, int duration, String intensity) {

        User user = usersRepository.findById(userId).orElse(null);

        if (user != null) {

            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);


            DailyLog dailyLog = dailyLogsRepository.findByUserAndLogDateTimeBetween(user, startOfDay, endOfDay);

            if (dailyLog != null) {

                Workout workout = new Workout();
                workout.setName(name);
                workout.setReps(reps);
                workout.setDuration(duration);
                workout.setIntensity(intensity);
                workout.setWorkoutTime(LocalDateTime.now());
                workout.setDailyLog(dailyLog);


                workoutsRepository.save(workout);
            }
        }
    }

    public void addWorkoutForUserTodayFromCSV(int userId) {
        // Get the user
        User user = usersRepository.findById(userId).orElse(null);

        if (user != null) {
            // Get today's date
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);


            DailyLog dailyLog = dailyLogsRepository.findByUserAndLogDateTimeBetween(user, startOfDay, endOfDay);

            if (dailyLog != null) {

                String csvFilePath = "C:\\Users\\Monika\\Desktop\\pythonn\\exercise_data.csv";
                Map<String, Integer> exerciseData = readExerciseDataFromCsv(csvFilePath);


                for (Map.Entry<String, Integer> entry : exerciseData.entrySet()) {
                    String exerciseName = entry.getKey();
                    int reps = entry.getValue();


                    if (!exerciseName.isEmpty()) {

                        Workout workout = new Workout();
                        workout.setName(exerciseName);
                        workout.setReps(reps);

                        workout.setWorkoutTime(LocalDateTime.now());
                        workout.setDailyLog(dailyLog);


                        workoutsRepository.save(workout);
                    }
                }
            }
        }
    }


    public Map<String, Integer> readExerciseDataFromCsv(String csvFilePath) {
        Map<String, Integer> exerciseData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean skipHeader = true; // Skip the first line (header)

            while ((line = reader.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String exerciseName = parts[0].trim();
                    int reps = Integer.parseInt(parts[1].trim());

                    // Update exercise data if reps are higher than existing value
                    exerciseData.putIfAbsent(exerciseName, reps);
                    exerciseData.computeIfPresent(exerciseName, (name, existingReps) -> Math.max(existingReps, reps));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exerciseData;
    }
}
