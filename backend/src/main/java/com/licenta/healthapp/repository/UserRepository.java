package com.licenta.healthapp.repository;

import com.licenta.healthapp.model.PreferredWorkouts;
import com.licenta.healthapp.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findById(int userId);


}
