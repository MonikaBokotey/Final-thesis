package com.licenta.healthapp.controller;

import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import com.licenta.healthapp.service.UserPreferencesService;
import com.licenta.healthapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class UserPreferencesController {

    private UserPreferencesService userPreferencesService;

    @Autowired
    private UserService userService;

    @Autowired
    public UserPreferencesController(UserPreferencesService userPreferencesService) {
        this.userPreferencesService = userPreferencesService;
    }

    @GetMapping("/getAll")
    public List<UserPreferences> getAllUserPreferences() {
        return userPreferencesService.getAllUserPreferences();
    }

    @GetMapping("/getUserPreferences/{userId}")
    public ResponseEntity<List<UserPreferences>> getUserPreferences(@PathVariable("userId") User user) {
        List<UserPreferences> userPreferences = userPreferencesService.getUserPreferences(user);
        return ResponseEntity.ok(userPreferences);
    }


    @PostMapping("/addUserPreferences/{userId}")
    public ResponseEntity<UserPreferences> addUserPreferences(@PathVariable("userId") Integer userId, @RequestBody UserPreferences userPreferences) {
        User user = userService.getUserById(userId);
        if(user != null) {
            userPreferences.setUser(user);
            UserPreferences newUserPreferences = userPreferencesService.addUserPreferences(userPreferences);
            return ResponseEntity.ok(newUserPreferences);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/updateTargetWeight/{userId}")
    public ResponseEntity<UserPreferences> updateTargetWeight(
            @PathVariable("userId") int userId,
            @RequestParam int targetWeight) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        UserPreferences updatedUserPreferences = userPreferencesService.updateTargetWeight(user, targetWeight);
        return ResponseEntity.ok(updatedUserPreferences);
    }

}
