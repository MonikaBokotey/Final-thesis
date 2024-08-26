package com.licenta.healthapp;

import com.licenta.healthapp.controller.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HealthappApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(HealthappApplication.class, args);
		UserPreferencesController ec = context.getBean(UserPreferencesController.class);
		PreferredFoodsController ec2=context.getBean(PreferredFoodsController.class);
		DailyLogController ec3=context.getBean(DailyLogController.class);
		PreferredWorkoutsController ec4=context.getBean(PreferredWorkoutsController.class);
		LoginController ec5=context.getBean(LoginController.class);
		MealController ec6=context.getBean(MealController.class);
		WorkoutController ec7=context.getBean(WorkoutController.class);
		UserController ec8=context.getBean(UserController.class);
		ec.toString();
		ec2.toString();
		ec3.toString();
		ec4.toString();
		ec5.toString();
		ec6.toString();
		ec7.toString();
		ec8.toString();
	}

}
