package com.licenta.healthapp.repository;

import com.licenta.healthapp.model.Meal;
import com.licenta.healthapp.model.PreferredFoods;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface MealsRepository extends CrudRepository<Meal,Integer> {

    @Query(value = "SELECT m FROM Meal m INNER JOIN m.dailyLog dl INNER JOIN dl.user u WHERE u.user_id = :userId AND dl.logDateTime BETWEEN :startOfDay AND :endOfDay")
    List<Meal> findMealsByUserIdAndCurrentDate(@Param("userId") int userId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query(value = "SELECT m FROM Meal m INNER JOIN m.dailyLog dl INNER JOIN dl.user u WHERE u.user_id = :userId")
    List<Meal> findAllMealsByUserId(@Param("userId") int userId);

}
