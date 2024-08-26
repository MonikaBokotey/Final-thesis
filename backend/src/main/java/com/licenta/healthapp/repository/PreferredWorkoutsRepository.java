package com.licenta.healthapp.repository;

import com.licenta.healthapp.model.PreferredFoods;
import com.licenta.healthapp.model.PreferredWorkouts;
import com.licenta.healthapp.model.UserPreferences;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferredWorkoutsRepository extends CrudRepository<PreferredWorkouts,Integer> {

    List<PreferredWorkouts> findAll();
}
