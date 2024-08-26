package com.licenta.healthapp.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "PreferredFoods")
public class PreferredFoods implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int preferred_food_id;

    @ManyToOne
    @JoinColumn(name = "user_preferences_id", nullable = false)
    private UserPreferences userPreferences;

    @Column(name = "food_name")
    private String food_name;

    public PreferredFoods() {
    }

    public int getPreferred_food_id() {
        return preferred_food_id;
    }

    public void setPreferred_food_id(int preferred_food_id) {
        this.preferred_food_id = preferred_food_id;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public String getFoodName() {
        return food_name;
    }

    public void setFoodName(String foodName) {
        this.food_name = foodName;
    }
}
