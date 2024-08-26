package com.licenta.healthapp.repository;
import com.licenta.healthapp.model.PreferredFoods;
import com.licenta.healthapp.model.User;
import com.licenta.healthapp.model.UserPreferences;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferredFoodsRepository extends CrudRepository<PreferredFoods,Integer>{

    List<PreferredFoods> findAll();


    @Query(value = "SELECT pf FROM PreferredFoods pf INNER JOIN pf.userPreferences up INNER JOIN up.user u WHERE u.user_id = :userId")
    List<PreferredFoods> findPreferredFoodsByUserId(@Param("userId") int userId);


    @Query(value = "SELECT pf FROM PreferredFoods pf INNER JOIN pf.userPreferences up INNER JOIN up.user u WHERE u.user_id = :userId AND pf.food_name = :foodName")
    PreferredFoods findPreferredFoodByUserIdAndFoodName(@Param("userId") int userId, @Param("foodName") String foodName);

}
