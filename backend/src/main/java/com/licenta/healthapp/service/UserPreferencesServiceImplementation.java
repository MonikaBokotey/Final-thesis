package com.licenta.healthapp.service;

import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import com.licenta.healthapp.repository.UserPreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferencesServiceImplementation implements UserPreferencesService {
    @Autowired
    private  UserPreferencesRepository userPreferencesRepository;


    public UserPreferencesServiceImplementation(UserPreferencesRepository userPreferencesRepository) {
        this.userPreferencesRepository = userPreferencesRepository;
    }

    @Override
    public List<UserPreferences> getAllUserPreferences() {
        return userPreferencesRepository.findAll();
    }

    public List<UserPreferences> getUserPreferences(User user) {
        return userPreferencesRepository.findByUser(user);
    }

    @Override
    public UserPreferences addUserPreferences(UserPreferences userPreferences) {
        return userPreferencesRepository.save(userPreferences);
    }


    @Override
    public UserPreferences createUserPreferencesForUser(User user) {
        UserPreferences newUserPreferences = new UserPreferences();
        newUserPreferences.setUser(user);
        return userPreferencesRepository.save(newUserPreferences);
    }

    public UserPreferences setTargetWeightForUser(User user, int targetWeight) {
        // Retrieve the existing UserPreferences for the user, or create a new one if it doesn't exist
        List<UserPreferences> preferencesList = userPreferencesRepository.findByUser(user);
        UserPreferences userPreferences;
        if (preferencesList.isEmpty()) {

            userPreferences = new UserPreferences();
            userPreferences.setUser(user);
        } else {

            userPreferences = preferencesList.get(0);
        }


        userPreferences.setTarget_weight(targetWeight);


        return userPreferencesRepository.save(userPreferences);
    }

    @Override
    public UserPreferences updateTargetWeight(User user, int targetWeight) {
        List<UserPreferences> userPreferencesList = userPreferencesRepository.findByUser(user);
        if (!userPreferencesList.isEmpty()) {
            UserPreferences userPreferences = userPreferencesList.get(0);
            userPreferences.setTarget_weight(targetWeight);
            return userPreferencesRepository.save(userPreferences);
        }
        throw new RuntimeException("User preferences not found for user: " + user.getUser_id());
    }
}
