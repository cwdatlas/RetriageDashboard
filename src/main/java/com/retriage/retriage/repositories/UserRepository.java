package com.retriage.retriage.repositories;

import com.retriage.retriage.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmailIgnoreCase(String email);
    //This is empty, intentionally

}
