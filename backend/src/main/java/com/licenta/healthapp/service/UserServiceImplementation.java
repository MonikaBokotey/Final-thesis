package com.licenta.healthapp.service;

import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import com.licenta.healthapp.repository.UserPreferencesRepository;
import com.licenta.healthapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService{

    @Autowired
    private UserRepository userRepository;

    private UserPreferences userPreferences;

    @Autowired
    private UserPreferencesServiceImplementation userPreferencesService;

    @Autowired
    private UserPreferencesRepository userPreferencesRepository;

    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> signUpUser(String username, String password, String email, String gender, double weight, double height, int targetWeight) {

        Optional<User> existingUserOptional = userRepository.findByUsername(username);
        if (existingUserOptional.isPresent()) {

            return Optional.empty();
        }


        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setGender(gender);
        newUser.setWeight(weight);
        newUser.setHeight(height);


        User savedUser = userRepository.save(newUser);


        userPreferencesService.setTargetWeightForUser(savedUser, targetWeight);


        return Optional.of(savedUser);
    }



    @Override
    public User updateWeight(Integer userId, double weight) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setWeight(weight);
            return userRepository.save(user);
        } else {
            return null;
        }
    }

    @Override
    public User updateHeight(Integer userId, double height) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setHeight(height);
            return userRepository.save(user);
        } else {
            return null;
        }
    }

    @Override
    public User updateAge(Integer userId, int age) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAge(age);
            return userRepository.save(user);
        } else {
            return null;
        }
    }

    @Override
    public User getUserById(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }
}
