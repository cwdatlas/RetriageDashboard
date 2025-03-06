package com.retriage.retriage.services;

import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UserServiceImp implements UserService {

    // Add Logger for keeping track of any errors
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    private final UserRepository userRepository;

    /**
     * User Service constructor
     *
     * @param userRepository Repository declared in UserServiceImp
     */
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.debug("UserServiceImp constructor: userRepository instance = {}", userRepository); // Debug log
    }

    /**
     * Saves a user
     *
     * @param user The User to be saved
     * @return The saved User
     */
    public User saveUser(@Valid User user) {
        //Create or Update the User
        logger.info("saveUser: User saved with ID: {}", user.getId()); // Log successful save

        return userRepository.save(user);
    }

    /**
     * Finds all currently saved User accounts
     *
     * @return Every user account
     */
    public List<User> findAllUsers() {
        logger.debug("findAllUsers: About to call userRepository.findAll()");
        List<User> users = userRepository.findAll();
        logger.debug("findAllUsers: Retrieved {} users", users.size()); // Log the result
        return users;

    }

    /**
     * Finds a User via their ID
     *
     * @param id The ID of the User you're looking for
     * @return The User object assigned to the passed in ID
     */
    public Optional<User> findUserById(Long id) {
        logger.debug("findUserById: About to call userRepository.findById({})", id); // Corrected log message
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.debug("findUserById: Found user with ID: {}", id);
        } else {
            logger.warn("findUserById: No user found with ID: {}", id);
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
    @Override
    public User updateUser(Long id, @Valid User user) {
        if (!userRepository.existsById(id)) {
            logger.error("updateUser: User with id {} not found for update.", id);
            return null;
        }
        // Manually enforce email validation
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            logger.error("updateUser: Invalid email format '{}'", user.getEmail());
            return null; // Return null if email is invalid
        }
        //Check if first name is blank/null
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            logger.error("updateUser: First name cannot be blank.");
            return null; // Reject update
        }
        //Check if Role is blank/null
        if (user.getRole() == null) {
            logger.error("updateUser: Role cannot be null.");
            return null; // Reject update
        }
        user.setId(id);
        return userRepository.save(user);
    }


    /**
     * Remove a User from saved list.
     *
     * @param id The ID of the director to be deleted
     */
    public void deleteUserById(Long id) {
        logger.debug("deleteUserById: Checking if user with ID {} exists", id);

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("deleteUserById: User deleted successfully with ID: {}", id); // Log successful deletion
        } else {
            String errorMessage = "User with ID " + id + " does not exist.";
            logger.warn("deleteUserById: {}", errorMessage); // Use {} formatting
            throw new RuntimeException(errorMessage); // Still throw the exception
        }

    }
}
