package com.licenta.healthapp.service;

import com.licenta.healthapp.model.DailyLog;
import com.licenta.healthapp.model.Meal;
import com.licenta.healthapp.model.User;
import com.licenta.healthapp.repository.DailyLogRepository;
import com.licenta.healthapp.repository.MealsRepository;
import com.licenta.healthapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MealsServiceImplementation implements MealsService{

   @Autowired
   private MealsRepository mealsRepository;
    @Autowired
    private DailyLogRepository dailyLogsRepository;

    @Autowired
    private UserRepository usersRepository;


    public Meal insertMeal(Meal meal) {
        return mealsRepository.save(meal);
    }

    public void addMealForUserToday(int userId, String mealName, double calories, double carbohydrates, double fats, double protein, LocalDateTime mealTime) {

        User user = usersRepository.findById(userId).orElse(null);

        if (user != null) {

            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);


            DailyLog dailyLog = dailyLogsRepository.findByUserAndLogDateTimeBetween(user, startOfDay, endOfDay);

            if (dailyLog != null) {

                Meal meal = new Meal();
                meal.setMealName(mealName);
                meal.setCalories(calories);
                meal.setCarbohydrates(carbohydrates);
                meal.setFats(fats);
                meal.setProtein(protein);
                meal.setMealTime(mealTime);
                meal.setDailyLog(dailyLog);


                mealsRepository.save(meal);
            }
        }
    }
}
