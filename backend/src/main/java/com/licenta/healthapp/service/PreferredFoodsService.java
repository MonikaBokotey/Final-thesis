package com.licenta.healthapp.service;
import com.licenta.healthapp.model.PreferredFoods;
import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PreferredFoodsService {

    List<PreferredFoods> getAllFoodPreferences();

    //List<PreferredFoods> getPreferredFoods(User user);

//    List<PreferredFoods> getPreferences(UserPreferences userPreferences);

    public void addPreferredFoodForUser(int userId, String foodName);

    public boolean userHasPreferences(int userId);

    public void deletePreferredFoodForUser(int userId, String foodName);
}
