package com.retriage.retriage.services;

import com.retriage.retriage.models.User;

import java.util.List;
import java.util.Optional;

/**
 *
 */
//TODO add all CRUD functions. Create Read Update and Delete. Some are already here, but more needs to be done
public interface UserService {
    User saveUser(User user); // Create

    List<User> findAllUsers(); // Read

    Optional<User> findUserById(Long id); // Read by ID

    User updateUser(Long id, User user); // Update

    void deleteUserById(Long id); // Delete

    User getUserByEmail(String email);

    User getUserFromToken(String token);
}
