package com.licenta.healthapp.service;

import com.licenta.healthapp.model.Meal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface MealsService {
     Meal insertMeal(Meal meal);
     void addMealForUserToday(int userId, String mealName, double calories, double carbohydrates, double fats, double protein, LocalDateTime mealTime);
}
