package com.licenta.healthapp.service;

import com.licenta.healthapp.model.PreferredWorkouts;

import com.licenta.healthapp.repository.PreferredWorkoutsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferredWorkoutsServiceImplementation implements PreferredWorkoutsService{
    @Autowired
    private PreferredWorkoutsRepository preferredWorkoutsRepository;

    public PreferredWorkoutsServiceImplementation(PreferredWorkoutsRepository preferredWorkoutsRepository) {
        this.preferredWorkoutsRepository = preferredWorkoutsRepository;
    }

    @Override
    public List<PreferredWorkouts>  getAllWorkoutPreferences() {
        return preferredWorkoutsRepository.findAll();
    }
}
