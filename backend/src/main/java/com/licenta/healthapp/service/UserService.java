package com.licenta.healthapp.service;

import com.licenta.healthapp.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    public Optional<User> authenticateUser(String username, String password);

    public Optional<User> signUpUser(String username, String password, String email, String gender, double weight, double height, int targetWeight);

    public User updateHeight(Integer userId, double height);
    public User updateWeight(Integer userId, double weight);

    public User updateAge(Integer userId, int age);
    public User getUserById(Integer userId);
}
