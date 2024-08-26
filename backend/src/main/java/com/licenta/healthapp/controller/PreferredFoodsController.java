package com.licenta.healthapp.controller;

import com.licenta.healthapp.model.PreferredFoods;
import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import com.licenta.healthapp.repository.PreferredFoodsRepository;
import com.licenta.healthapp.service.PreferredFoodsService;
import com.licenta.healthapp.service.UserPreferencesService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class PreferredFoodsController {

    private PreferredFoodsService preferredFoodsService;
    private UserPreferencesService preferencesService;
    private PreferredFoodsRepository preferredFoodsRepository;

    @Autowired
    public PreferredFoodsController(PreferredFoodsService preferredFoodsService, PreferredFoodsRepository preferredFoodsRepository) {
        this.preferredFoodsService = preferredFoodsService;
        this.preferredFoodsRepository = preferredFoodsRepository;
    }



    @GetMapping("/getPreferredFoods")
    public List<PreferredFoods> getAllFoodPreferences() {
        return preferredFoodsService.getAllFoodPreferences();
    }


    // THIS WORKS--------


    @GetMapping("/preferredFoods/{userId}")
    public ResponseEntity<?> getUserPreferredFoods(@PathVariable("userId") int userId) {
        try {
            List<PreferredFoods> preferredFoods = preferredFoodsRepository.findPreferredFoodsByUserId(userId);
            return ResponseEntity.ok(preferredFoods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    //------------------------------------

    @GetMapping("/preferredFoodsTokenized")
    public ResponseEntity<?> getUserPreferredFoods(HttpServletRequest request) {


        try {
            // Extract the token from the Authorization header
            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authorization token provided");
            }


            // Validate the token and extract the userId
            int userId = validateTokenAndGetUserId(token);

            // Fetch the preferred foods
            List<PreferredFoods> preferredFoods = preferredFoodsRepository.findPreferredFoodsByUserId(userId);

            return ResponseEntity.ok(preferredFoods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }




    public int validateTokenAndGetUserId(String token) {
        int userId = Integer.parseInt(token);
        return userId;
    }

    @PostMapping("/addPreferredFood/{userId}")
    public ResponseEntity<Void> addPreferredFoodForUser(@PathVariable("userId") int userId, @RequestBody String foodName) {
        preferredFoodsService.addPreferredFoodForUser(userId, foodName);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/hasPreferences/{userId}")
    public ResponseEntity<?> userHasPreferences(@PathVariable("userId") int userId) {
        try {
            boolean hasPreferences = preferredFoodsService.userHasPreferences(userId);
            if (hasPreferences) {
                return ResponseEntity.ok("User has preferences saved.");
            } else {
                return ResponseEntity.ok("User has no preferences saved.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletePreferredFood/{userId}")
    public ResponseEntity<Void> deletePreferredFoodForUser(@PathVariable("userId") int userId, @RequestBody Map<String, String> body) {
        try {
            String foodName = body.get("foodName");
            preferredFoodsService.deletePreferredFoodForUser(userId, foodName);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/findPreferredFood")
    public ResponseEntity<?> findPreferredFood(@RequestBody Map<String, Object> params) {
        try {
            int userId = (int) params.get("userId");
            String foodName = (String) params.get("foodName");
            PreferredFoods preferredFood = preferredFoodsRepository.findPreferredFoodByUserIdAndFoodName(userId, foodName);
            if (preferredFood != null) {
                return ResponseEntity.ok(preferredFood);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Preferred food not found for the given user ID and food name.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }


}
