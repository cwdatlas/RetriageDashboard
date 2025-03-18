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
     * User Service constructor
     *
     * @param userRepository Repository declared in UserServiceImp
     */
    public UserServiceImp(UserRepo userRepository) {
        this.userRepository = userRepository;
        logger.debug("UserServiceImp constructor: userRepository instance = {}", userRepository); // Debug logger
    }

    /**
     * Saves a user
     *
     * @param user The User to be saved
     * @return The saved User
     */
    public User saveUser(User user) {
        logger.info("** Starting to save new user **"); // Log entry into the method
        logger.debug("saveUser: User details - {}", user); // Debug log to show user details
        validateUser(user);
        logger.debug("saveUser: User validation passed."); // Debug log after validation
        //Save the user with a log message
        User savedUser = userRepository.save(user);
        logger.info("saveUser: User saved successfully with ID: {}", savedUser.getId()); // Log successful save with ID
        return savedUser;
    }

    /**
     * Finds all currently saved User accounts
     *
     * @return Every user account
     */
    public List<User> findAllUsers() {
        logger.info("** Starting to retrieve all users. **"); // Log entry into the method
        logger.debug("findAllUsers: About to call userRepository.findAll()");
        List<User> users = userRepository.findAll();
        logger.debug("findAllUsers: Retrieved {} users", users.size()); // Log the result count in debug
        logger.info("findAllUsers: Successfully retrieved {} users.", users.size()); // Log successful retrieval in info
        return users;
    }

    /**
     * Finds a User via their ID
     *
     * @param id The ID of the User you're looking for
     * @return The User object assigned to the passed in ID
     */
    public Optional<User> findUserById(Long id) {
        logger.debug("findUserById: About to call userRepository.findById({})", id); // Corrected logger message
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.debug("findUserById: Found user with ID: {}", id); // Debug log when user is found
        } else {
            logger.warn("findUserById: No user found with ID: {}", id); // Warn log when user is not found
        }
        return user;
    }

    /**
     * Updates a user with a new specified ID
     *
     * @param id   ID to change to
     * @param user User to update
     * @return Saving the newly updated User
     */
    public User updateUser(Long id, User user) {
        logger.info("** Starting to update user with ID: {} **", id); // Log entry into the update method
        logger.debug("updateUser: User details for update - ID: {}, User: {}", id, user); // Debug log of user details
        if (!userRepository.existsById(id)) {
            logger.error("updateUser: User with id {} not found for update.", id); // Error log if user not found
            return null; // Consider throwing an exception instead of returning null, and logging a WARN before throwing
        }
        logger.debug("updateUser: User with ID {} exists. Proceeding with update.", id); // Debug log if user exists
        validateUser(user);
        logger.debug("updateUser: User validation passed for update."); // Debug log after validation
        user.setId(id);
        User updatedUser = userRepository.save(user);
        logger.info("updateUser: User updated successfully with ID: {}", updatedUser.getId()); // Log successful update
        return updatedUser;
    }

    /**
     * Remove a User from saved list.
     *
     * @param id The ID of the director to be deleted
     */
    public void deleteUserById(Long id) {
        logger.info("** Starting to delete user with ID: {} **", id); // Log entry into delete method
        logger.debug("deleteUserById: Checking if user with ID {} exists", id);

        if (userRepository.existsById(id)) {
            logger.debug("deleteUserById: User with ID {} exists. Proceeding with deletion.", id); // Debug log if user exists
            userRepository.deleteById(id);
            logger.info("deleteUserById: User deleted successfully with ID: {}", id); // Log successful deletion
        } else {
            String errorMessage = "User with ID " + id + " does not exist.";
            logger.warn("deleteUserById: {}", errorMessage); // Warn log when user not found for deletion
            logger.debug("deleteUserById: Throwing RuntimeException - {}", errorMessage); // Debug log before throwing exception
            throw new RuntimeException(errorMessage); // Still throw the exception
        }
    }

    @Override
    public User getUserByEmail(String email) {
        if (email != null) {
            try {
                List<User> users = userRepository.findByEmailIgnoreCase(email);
                if (users.size() == 1) {
                    return users.get(0);
                } else if (users.size() > 1) {
                    logger.warn("GetUserByName: More than one users found with email '{}'", email);
                } else {
                    logger.debug("GetUserByName: Zero user found with email '{}'", email);
                }
            } catch (Exception e) {
                logger.warn("getUserByName:", e);
            }
        }
        return null;
    }

    /**
     * Validates a User object.
     */
    private void validateUser(User user) {
        logger.debug("** Starting user validation **"); // Debug log at start of validation
        if (user == null) {
            logger.warn("validateUser: User object is null."); // Warn log for null user
            throw new IllegalArgumentException("User object cannot be null.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            logger.warn("validateUser: User email is invalid - {}", user.getEmail()); // Warn log for invalid email
            throw new IllegalArgumentException("User email must be a valid email address.");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            logger.warn("validateUser: User first name is null or empty."); // Warn log for empty first name
            throw new IllegalArgumentException("User first name cannot be null or empty.");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            logger.warn("validateUser: User last name is null or empty."); // Warn log for empty last name
            throw new IllegalArgumentException("User last name cannot be null or empty.");
        }
        if (user.getRole() == null || Objects.equals(user.getRole().toString(), " ")) {
            logger.warn("validateUser: User role is null or default."); // Warn log for invalid role
            throw new IllegalArgumentException("User must have at least one role assigned.");
        }
        logger.debug("User validation passed successfully."); // Debug log if validation passes
    }
}
