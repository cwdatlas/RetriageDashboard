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

    User getUserByEmail(String email);
}
