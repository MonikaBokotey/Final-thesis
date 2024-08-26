package com.licenta.healthapp.controller;

import com.licenta.healthapp.model.Meal;
import com.licenta.healthapp.repository.MealsRepository;
import com.licenta.healthapp.service.MealsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin
public class MealController {
    @Autowired
    private MealsRepository mealsRepository;

    @Autowired
    private MealsService mealService;

    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.atTime(23, 59, 59);

    @GetMapping("/users/{userId}/meals")
    public List<Meal> getMealsByUserIdAndCurrentDate(@PathVariable("userId") int userId) {
        return mealsRepository.findMealsByUserIdAndCurrentDate(userId,startOfDay,endOfDay);
    }

    @PostMapping("/users/{userId}/getMeals")
    public void addMealForUserToday(@PathVariable("userId") int userId, @RequestBody Meal meal) {
        mealService.addMealForUserToday(userId, meal.getMealName(), meal.getCalories(), meal.getCarbohydrates(), meal.getFats(), meal.getProtein(), meal.getMealTime());
    }


    @GetMapping("/users/{userId}/allMeals")
    public List<Meal> getAllMealsByUserId(@PathVariable("userId") int userId) {
        return mealsRepository.findAllMealsByUserId(userId);
    }


}
