package com.licenta.healthapp.repository;

import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPreferencesRepository extends CrudRepository<UserPreferences,Integer>{

    List<UserPreferences> findAll();

    List<UserPreferences> findByUser(User user);


}
