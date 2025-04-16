package com.retriage.retriage.services;

import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Validated
public class UserServiceImp implements UserService {

    // Add Logger for keeping track of any errors
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private final UserRepo userRepository;

    /**
     * UserServiceImp
     * User Service constructor
     *
     * @param userRepository Repository declared in UserServiceImp
     */
    public UserServiceImp(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * saveUser
     * Saves a user
     *
     * @param user The User to be saved
     * @return The saved User
     */
    public User saveUser(User user) {
        try {
            validateUser(user);
            User savedUser = userRepository.save(user);
            logger.info("saveUser - User saved successfully with ID: {}", savedUser.getId());
            return savedUser;
        } catch (IllegalArgumentException e) {
            logger.warn("saveUser - User save failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * getUserByEmail
     * Returns the user associated with the specified Email
     */
    @Override
    public User getUserByEmail(String email) {
        if (email != null) {
            try {
                List<User> users = userRepository.findByEmailIgnoreCase(email);
                if (users.size() == 1) {
                    logger.info("getUserByEmail - User found with email: {}", email);
                    return users.getFirst();
                } else if (users.size() > 1) {
                    logger.warn("getUserByEmail - Multiple users found with email: {}", email);
                } else {
                    logger.debug("getUserByEmail - No user found with email: {}", email);
                }
            } catch (Exception e) {
                logger.warn("getUserByEmail - Email search failed: {}", e.getMessage());
            }
        } else {
            logger.warn("getUserByEmail - Email is null.");
        }
        return null;
    }

    /**
     * validateUser
     * Validates a User object has all of its parameters properly filled out
     */
    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User object cannot be null.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("User email must be a valid email address.");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("User first name cannot be null or empty.");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("User last name cannot be null or empty.");
        }
        if (user.getRole() == null || Objects.equals(user.getRole().toString(), " ")) {
            throw new IllegalArgumentException("User must have at least one role assigned.");
        }
    }
}