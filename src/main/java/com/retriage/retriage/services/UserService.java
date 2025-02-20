package com.retriage.retriage.services;

import com.retriage.retriage.models.User;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface UserService {
    User saveUser(User user);

    List<User> findAllUsers();

    Optional<User> findUserById(Long id);

    void deleteUserById(Long id);
}
