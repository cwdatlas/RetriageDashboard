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
        logger.info("Entering UserServiceImp constructor with userRepository: {}", userRepository);
        this.userRepository = userRepository;
        logger.debug("UserServiceImp constructor: userRepository instance = {}", userRepository); // Debug logger
        logger.info("Exiting UserServiceImp constructor");
    }

    /**
     * saveUser
     * Saves a user
     *
     * @param user The User to be saved
     * @return The saved User
     */
    public User saveUser(User user) {
        logger.info("Entering saveUser with user: {}", user); // Log entry into the method
        logger.debug("saveUser: User details - {}", user); // Debug log to show user details
        try {
            validateUser(user);
            logger.debug("saveUser: User validation passed."); // Debug log after validation
            //Save the user with a log message
            User savedUser = userRepository.save(user);
            logger.info("saveUser: User saved successfully with ID: {}", savedUser.getId()); // Log successful save with ID
            logger.debug("saveUser: Saved user details - {}", savedUser);
            logger.info("Exiting saveUser, returning: {}", savedUser);
            return savedUser;
        } catch (IllegalArgumentException e) {
            logger.warn("saveUser: User validation failed: {}", e.getMessage());
            logger.info("Exiting saveUser, returning: null");
            return null;
        }
    }

    /**
     * findAllUsers
     * Finds all currently saved User accounts
     *
     * @return Every user account
     */
    public List<User> findAllUsers() {
        logger.info("Entering findAllUsers"); // Log entry into the method
        logger.debug("findAllUsers: About to call userRepository.findAll()");
        List<User> users = userRepository.findAll();
        logger.info("findAllUsers: Retrieved {} users.", users.size()); // Log successful retrieval in info
        logger.debug("findAllUsers: Retrieved user list: {}", users); // Log the result count in debug
        logger.info("Exiting findAllUsers, returning list of size: {}", users.size());
        return users;
    }

    /**
     * findUserById
     * Finds a User via their ID
     *
     * @param id The ID of the User you're looking for
     * @return The User object assigned to the passed in ID
     */
    public Optional<User> findUserById(Long id) {
        logger.info("Entering findUserById with id: {}", id);
        logger.debug("findUserById: About to call userRepository.findById({})", id); // Corrected logger message
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.debug("findUserById: Found user with ID: {}", id); // Debug log when user is found
            logger.info("findUserById: Found user with ID: {}", id);
        } else {
            logger.warn("findUserById: No user found with ID: {}", id); // Warn log when user is not found
        }
        logger.info("Exiting findUserById, returning Optional with value present: {}", user.isPresent());
        return user;
    }

    /**
     * updateUser
     * Updates a user with a new specified ID
     *
     * @param id   ID to change to
     * @param user User to update
     * @return Saving the newly updated User
     */
    public User updateUser(Long id, User user) {
        logger.info("Entering updateUser with id: {} and user: {}", id, user); // Log entry into the update method
        logger.debug("updateUser: User details for update - ID: {}, User: {}", id, user); // Debug log of user details
        if (!userRepository.existsById(id)) {
            logger.warn("updateUser: User with id {} not found for update.", id); // Error log if user not found
            logger.info("Exiting updateUser, returning: null");
            return null; // Consider throwing an exception instead of returning null, and logging a WARN before throwing
        }
        logger.debug("updateUser: User with ID {} exists. Proceeding with update.", id); // Debug log if user exists
        try {
            validateUser(user);
            logger.debug("updateUser: User validation passed for update."); // Debug log after validation
            user.setId(id);
            User updatedUser = userRepository.save(user);
            logger.info("updateUser: User updated successfully with ID: {}", updatedUser.getId()); // Log successful update
            logger.debug("updateUser: Updated user details - {}", updatedUser);
            logger.info("Exiting updateUser, returning: {}", updatedUser);
            return updatedUser;
        } catch (IllegalArgumentException e) {
            logger.warn("updateUser: User validation failed: {}", e.getMessage());
            logger.info("Exiting updateUser, returning: null");
            return null;
        }
    }

    /**
     * deleteUserById
     * Remove a User from saved list.
     *
     * @param id The ID of the director to be deleted
     */
    public void deleteUserById(Long id) {
        logger.info("Entering deleteUserById with id: {}", id); // Log entry into delete method
        logger.debug("deleteUserById: Checking if user with ID {} exists", id);

        if (userRepository.existsById(id)) {
            logger.debug("deleteUserById: User with ID {} exists. Proceeding with deletion.", id); // Debug log if user exists
            userRepository.deleteById(id);
            logger.info("deleteUserById: User deleted successfully with ID: {}", id); // Log successful deletion
        } else {
            logger.warn("deleteUserById: User with ID {} does not exist.", id); // Warn log when user not found for deletion
            logger.debug("deleteUserById: Attempted to delete non-existent user with id: {}", id); // Debug log before throwing exception
            // Removed the RuntimeException as per previous style
        }
        logger.info("Exiting deleteUserById");
    }

    /**
     * getUserByEmail
     * Returns the user associated with the specified Email
     */
    @Override
    public User getUserByEmail(String email) {
        logger.info("Entering getUserByEmail with email: {}", email);
        if (email != null) {
            try {
                List<User> users = userRepository.findByEmailIgnoreCase(email);
                if (users.size() == 1) {
                    logger.debug("getUserByEmail: Found user with email '{}'", email);
                    logger.info("Exiting getUserByEmail, returning user: {}", users.get(0));
                    return users.get(0);
                } else if (users.size() > 1) {
                    logger.warn("getUserByEmail: More than one user found with email '{}'", email);
                } else {
                    logger.debug("getUserByEmail: No user found with email '{}'", email);
                }
            } catch (Exception e) {
                logger.warn("getUserByEmail: Exception occurred while searching for user with email '{}': {}", email, e.getMessage());
                logger.debug("getUserByEmail: Stack trace - ", e);
            }
        } else {
            logger.warn("getUserByEmail: Email is null.");
        }
        logger.info("Exiting getUserByEmail, returning: null");
        return null;
    }

    /**
     * validateUser
     * Validates a User object has all of it's parameters properly filled out
     */
    private void validateUser(User user) {
        logger.debug("Entering validateUser with user: {}", user); // Debug log at start of validation
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
        logger.debug("Exiting validateUser - User validation passed successfully."); // Debug log if validation passes
    }
}