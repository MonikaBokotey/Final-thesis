package com.licenta.healthapp.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "PreferredWorkouts")
public class PreferredWorkouts implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int preferred_workout_id;

    @ManyToOne
    @JoinColumn(name = "user_preferences_id", nullable = false)
    private UserPreferences userPreferences;

    @Column(name = "workout_id")
    private String workout_id;

    public PreferredWorkouts() {
    }

    @Column(name = "workout_rating")
    private String workout_rating;



    public int getPreferred_workout_id() {
        return preferred_workout_id;
    }

    public void setPreferred_workout_id(int preferred_workout_id) {
        this.preferred_workout_id = preferred_workout_id;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public String getWorkout_id() {
        return workout_id;
    }

    public void setWorkout_id(String workout_id) {
        this.workout_id = workout_id;
    }

    public String getWorkout_rating() {
        return workout_rating;
    }

    public void setWorkout_rating(String workout_rating) {
        this.workout_rating = workout_rating;
    }
}
