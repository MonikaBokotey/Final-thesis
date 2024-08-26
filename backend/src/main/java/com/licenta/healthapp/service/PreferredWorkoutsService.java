package com.licenta.healthapp.service;

import com.licenta.healthapp.model.PreferredFoods;
import com.licenta.healthapp.model.PreferredWorkouts;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PreferredWorkoutsService {
    List<PreferredWorkouts> getAllWorkoutPreferences();
}
