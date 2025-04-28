package com.retriage.retriage.services;

import com.retriage.retriage.models.User;

/**
 * Service interface defining the contract for managing {@link User} entities.
 * Implementations of this interface handle the business logic related to users,
 * including creation, retrieval, and potentially updates.
 */
public interface UserService {

    /**
     * Saves a given {@link User} entity.
     * This method can be used for both creating a new user and updating an existing one.
     *
     * @param user The {@link User} entity to save.
     * @return The saved {@link User} entity.
     */
    User saveUser(User user); // Create/Update operation

    /**
     * Retrieves a {@link User} entity by their email address.
     * Email is typically used as a unique identifier for users.
     *
     * @param email The email address of the user to find.
     * @return The {@link User} entity if found, otherwise {@code null}.
     */
    User getUserByEmail(String email);
}