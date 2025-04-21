package com.retriage.retriage.repositories;

import com.retriage.retriage.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link User} entities.
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 * <p>
 * This interface interacts with the "users" table in the database.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    /**
     * Finds and returns a list of {@link User} entities with the given email address, ignoring case.
     * Spring Data JPA automatically generates the query for this method based on its name.
     * Since email is expected to be unique, this list should typically contain at most one element.
     *
     * @param email The email address to search for (case-insensitive).
     * @return A {@link List} of {@link User} objects found with the specified email. Returns an empty list if no user is found.
     */
    List<User> findByEmailIgnoreCase(String email);
    //This is empty, intentionally

}