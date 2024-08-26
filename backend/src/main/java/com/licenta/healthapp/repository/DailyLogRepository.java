package com.licenta.healthapp.repository;

import com.licenta.healthapp.model.DailyLog;
import com.licenta.healthapp.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyLogRepository extends CrudRepository<DailyLog, Integer> {
    List<DailyLog> findAll();
    @Query("SELECT dl FROM DailyLog dl WHERE dl.user = :user AND dl.logDateTime BETWEEN :startOfDay AND :endOfDay")
    DailyLog findByUserAndLogDateTimeBetween(@Param("user") User user, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
