package com.licenta.healthapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "UserPreferences")
public class UserPreferences implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_preferences_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "preferred_intensity_level")
    private String preferred_intensity_level;

    @Column(name = "preferred_workout_duration")
    private int preferred_workout_duration;

    @Column(name = "target_weight")
    private int target_weight;

    @Column(name = "dietary_restrictions")
    private String dietary_restrictions;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public UserPreferences() {
    }

    @OneToMany(mappedBy = "userPreferences", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PreferredFoods> preferredFoods;

    public List<PreferredFoods> getPreferredFoods() {
        return preferredFoods;
    }

    public void setPreferredFoods(List<PreferredFoods> preferredFoods) {
        this.preferredFoods = preferredFoods;
    }

    public int getUser_preferences_id() {
        return user_preferences_id;
    }

    public void setUser_preferences_id(int user_preferences_id) {
        this.user_preferences_id = user_preferences_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPreferred_intensity_level() {
        return preferred_intensity_level;
    }

    public void setPreferred_intensity_level(String preferred_intensity_level) {
        this.preferred_intensity_level = preferred_intensity_level;
    }

    public int getPreferred_workout_duration() {
        return preferred_workout_duration;
    }

    public void setPreferred_workout_duration(int preferred_workout_duration) {
        this.preferred_workout_duration = preferred_workout_duration;
    }

    public int getTarget_weight() {
        return target_weight;
    }

    public void setTarget_weight(int target_weight) {
        this.target_weight = target_weight;
    }

    public String getDietary_restrictions() {
        return dietary_restrictions;
    }

    public void setDietary_restrictions(String dietary_restrictions) {
        this.dietary_restrictions = dietary_restrictions;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
