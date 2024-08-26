package com.licenta.healthapp.repository;

import com.licenta.healthapp.model.Workout;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutRepository extends CrudRepository<Workout,Integer> {

    @Query(value = "SELECT w FROM Workout w INNER JOIN w.dailyLog dl INNER JOIN dl.user u WHERE u.user_id = :userId AND dl.logDateTime BETWEEN :startOfDay AND :endOfDay")
    List<Workout> findWorkoutsByUserIdAndCurrentDate(@Param("userId") int userId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query(value = "SELECT w FROM Workout w INNER JOIN w.dailyLog dl INNER JOIN dl.user u WHERE u.user_id = :userId")
    List<Workout> findAllWorkoutsByUserId(@Param("userId") int userId);


}