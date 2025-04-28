package com.retriage.retriage.services;

import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Implementation of the {@link UserService} interface.
 * Provides the concrete business logic for managing {@link User} entities,
 * interacting with the database via {@link UserRepo}.
 * Includes validation for user data before persistence.
 */
@Service
@Validated // Enables method validation based on JSR 303/349/380 annotations
public class UserServiceImp implements UserService {

    /**
     * Logger for this service implementation.
     */
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    /**
     * Repository for accessing and managing User entities in the database.
     */
    private final UserRepo userRepository;

    /**
     * Constructs an instance of {@code UserServiceImp}.
     *
     * @param userRepository The {@link UserRepo} used for database operations on users.
     */
    public UserServiceImp(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Saves a user (Create/Update) in the database.
     * Performs validation using {@link #validateUser(User)} before attempting to save.
     *
     * @param user The {@link User} entity to be saved.
     * @return The saved {@link User} entity if validation and saving are successful, otherwise {@code null}.
     */
    public User saveUser(User user) {
        try {
            validateUser(user); // Validate the user object
            User savedUser = userRepository.save(user);
            logger.info("saveUser - User saved successfully with ID: {}", savedUser.getId());
            return savedUser;
        } catch (IllegalArgumentException e) {
            logger.warn("saveUser - User save failed: {}", e.getMessage());
            return null; // Return null if validation fails
        } catch (Exception e) {
            logger.error("saveUser - An unexpected error occurred while saving user: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Retrieves a user by their email address, performing a case-insensitive search.
     * Expects at most one user to be found for a given email due to uniqueness constraints.
     *
     * @param email The email address (String) of the user to look for. Can be null.
     * @return The {@link User} entity if found, otherwise {@code null}. Returns null if the input email is null, if multiple users are found with the same email, or if an error occurs during the database search.
     */
    @Override
    public User getUserByEmail(String email) {
        if (email != null && !email.trim().isEmpty()) { // Check for null or empty email
            try {
                // findByEmailIgnoreCase is provided by Spring Data JPA based on repository method naming
                List<User> users = userRepository.findByEmailIgnoreCase(email);
                if (users.size() == 1) {
                    logger.info("getUserByEmail - User found with email: {}", email);
                    return users.getFirst();
                } else if (users.size() > 1) {
                    logger.warn("getUserByEmail - Multiple users found with email: {}", email);
                    return null; // Indicate a data integrity issue
                } else {
                    logger.debug("getUserByEmail - No user found with email: {}", email);
                    return null; // User not found
                }
            } catch (Exception e) {
                // Catch potential exceptions during repository interaction
                logger.warn("getUserByEmail - Email search failed for {}: {}", email, e.getMessage());
                return null;
            }
        } else {
            logger.warn("getUserByEmail - Email input is null or empty.");
            return null; // Indicate invalid input
        }
    }

    /**
     * Validates a User object to ensure required fields are properly filled out.
     * Checks for null user object, valid email format and presence, non-empty first and last names,
     * and that a role is assigned.
     *
     * @param user The {@link User} object to validate.
     * @throws IllegalArgumentException if the user object or its required fields are invalid or missing.
     */
    private void validateUser(User user) {
        if (user == null) {
            logger.warn("validateUser - User object is null.");
            throw new IllegalArgumentException("User object cannot be null.");
        }
        // Basic email validation
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            logger.warn("validateUser - User email is invalid or empty: {}", user.getEmail());
            throw new IllegalArgumentException("User email must be a valid email address.");
        }
        // Check first name
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            logger.warn("validateUser - User first name is null or empty.");
            throw new IllegalArgumentException("User first name cannot be null or empty.");
        }
        // Check last name
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            logger.warn("validateUser - User last name is null or empty.");
            throw new IllegalArgumentException("User last name cannot be null or empty.");
        }
        // Check role
        if (user.getRole() == null) { // Checking for null enum instance
            logger.warn("validateUser - User role is null.");
            throw new IllegalArgumentException("User must have a role assigned.");
        }
    }
}