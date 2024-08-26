package com.licenta.healthapp.service;

import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserPreferencesService {
    List<UserPreferences> getAllUserPreferences();

    List<UserPreferences> getUserPreferences(User user);

    UserPreferences addUserPreferences(UserPreferences userPreferences);

   UserPreferences createUserPreferencesForUser(User user);

    UserPreferences setTargetWeightForUser(User user, int targetWeight);

    UserPreferences updateTargetWeight(User user, int targetWeight);

}
