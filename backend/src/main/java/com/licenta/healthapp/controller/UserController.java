package com.licenta.healthapp.controller;

import com.licenta.healthapp.model.User;
import com.licenta.healthapp.repository.UserRepository;
import com.licenta.healthapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            return ResponseEntity.ok().body(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/updateWeight/{id}")
    public ResponseEntity<User> updateWeight(@PathVariable(value = "id") Integer userId, @RequestBody Map<String, Double> weightMap) {
        Double weight = weightMap.get("weight");
        User user = userService.updateWeight(userId, weight);
        if(user != null) {
            return ResponseEntity.ok().body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/updateHeight/{id}")
    public ResponseEntity<User> updateHeight(@PathVariable(value = "id") Integer userId, @RequestBody Map<String, Double> heightMap) {
        Double height = heightMap.get("height");
        User user = userService.updateHeight(userId, height);
        if(user != null) {
            return ResponseEntity.ok().body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/updateAge/{id}")
    public ResponseEntity<User> updateAge(@PathVariable(value = "id") Integer userId, @RequestBody Map<String, Integer> ageMap) {
        Integer age = ageMap.get("age");
        User user = userService.updateAge(userId, age);
        if(user != null) {
            return ResponseEntity.ok().body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
