package com.licenta.healthapp.controller;

import com.licenta.healthapp.model.Workout;
import com.licenta.healthapp.repository.WorkoutRepository;
import com.licenta.healthapp.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutsRepository;

    @Autowired
    private WorkoutService workoutsService;

    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.atTime(23, 59, 59);


    @GetMapping("/users/{userId}/workouts")
    public List<Workout> getWorkoutsByUserIdAndCurrentDate(@PathVariable("userId") int userId) {
        return workoutsRepository.findWorkoutsByUserIdAndCurrentDate(userId, startOfDay, endOfDay);
    }

    @PostMapping("/users/{userId}/addWorkout")
    public void addWorkoutForUserToday(@PathVariable("userId") int userId, @RequestBody Workout workout) {
        workoutsService.addWorkoutForUserToday(userId, workout.getName(), workout.getReps(), workout.getDuration(), workout.getIntensity());
    }

    @PostMapping("/users/{userId}/addWorkoutFromCsv")
    public ResponseEntity<String> addWorkoutFromCsv(@PathVariable("userId") int userId) {

        String csvFilePath = "C:\\Users\\Monika\\Desktop\\pythonn\\exercise_data.csv";


        Map<String, Integer> exerciseData = workoutsService.readExerciseDataFromCsv(csvFilePath);

        workoutsService.addWorkoutForUserTodayFromCSV(userId);

        return new ResponseEntity<>("Workouts added successfully!", HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/allWorkouts")
    public List<Workout> getAllWorkoutsByUserId(@PathVariable("userId") int userId) {
        return workoutsRepository.findAllWorkoutsByUserId(userId);
    }

}
