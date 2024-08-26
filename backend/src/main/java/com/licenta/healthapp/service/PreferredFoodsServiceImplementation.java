package com.licenta.healthapp.service;
import com.licenta.healthapp.model.PreferredFoods;
import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import com.licenta.healthapp.repository.PreferredFoodsRepository;
import com.licenta.healthapp.repository.UserPreferencesRepository;
import com.licenta.healthapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreferredFoodsServiceImplementation implements PreferredFoodsService{


    @Autowired
    private PreferredFoodsRepository preferredFoodsRepository;

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;



    public PreferredFoodsServiceImplementation(PreferredFoodsRepository preferredFoodsRepository) {
        this.preferredFoodsRepository = preferredFoodsRepository;
    }

    @Override
    public List<PreferredFoods> getAllFoodPreferences() {
        return preferredFoodsRepository.findAll();
    }

    @Override
    public void addPreferredFoodForUser(int userId, String foodName) {
        User user = usersRepository.findById(userId).orElse(null);

        if (user != null) {
            UserPreferences userPreferences = userPreferencesRepository.findByUser(user).get(0); // assuming there's at least one UserPreferences for the user

            if (userPreferences != null) {
                PreferredFoods preferredFood = new PreferredFoods();
                preferredFood.setUserPreferences(userPreferences);
                preferredFood.setFoodName(foodName);
                preferredFoodsRepository.save(preferredFood);
            }
        }
    }

    @Override
    public boolean userHasPreferences(int userId) {
        List<PreferredFoods> preferredFoods = preferredFoodsRepository.findPreferredFoodsByUserId(userId);
        return !preferredFoods.isEmpty();
    }

    @Override
    public void deletePreferredFoodForUser(int userId, String foodName) {
        PreferredFoods preferredFood = preferredFoodsRepository.findPreferredFoodByUserIdAndFoodName(userId, foodName);
        if (preferredFood != null) {
            preferredFoodsRepository.delete(preferredFood);
        }
    }
}
