package com.licenta.healthapp.controller;

import com.licenta.healthapp.model.PreferredFoods;
import com.licenta.healthapp.model.PreferredWorkouts;
import com.licenta.healthapp.service.PreferredWorkoutsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class PreferredWorkoutsController {
    private PreferredWorkoutsService preferredWorkoutsService;

    @Autowired

    public PreferredWorkoutsController(PreferredWorkoutsService preferredWorkoutsService) {
        this.preferredWorkoutsService = preferredWorkoutsService;
    }

    @GetMapping("/getPreferredWorkout")
    public List<PreferredWorkouts> getAllWorkoutPreferences() {
        return preferredWorkoutsService.getAllWorkoutPreferences();
    }


}
