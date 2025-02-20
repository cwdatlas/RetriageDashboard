package com.retriage.retriage.repositories;

import com.retriage.retriage.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //This is empty, intentionally

}
