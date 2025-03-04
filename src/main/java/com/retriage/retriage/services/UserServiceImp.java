package com.retriage.retriage.services;

import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    // Add Logger for keeping track of any errors
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    /**
     * User Service constructor
     *
     * @param userRepository Repository declared in UserServiceImp
     */
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Saves a user
     *
     * @param user The User to be saved
     * @return The saved User
     */
    public User saveUser(User user) {
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
        return userRepository.findAll();
    }

    /**
     * Finds a User via their ID
     *
     * @param id The ID of the User you're looking for
     * @return The User object assigned to the passed in ID
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Updates a user with a new specified ID
     * @param id ID to change to
     * @param user User to update
     * @return Saving the newly updated User
     */
    @Override
    public User updateUser(Long id, User user) {
        if (!userRepository.existsById(id)) {
            String errorMessage = "User with id " + id + " not found for update.";
            logger.error("updateUser: " + errorMessage); // Keep logger.error, use method name in log
            return null; // Return null if the user doesn't exist
        }

        user.setId(id); // Ensure we maintain the correct ID
        return userRepository.save(user);
    }


    /**
     * Remove a User from saved list.
     *
     * @param id The ID of the director to be deleted
     */
    public void deleteUserById(Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("deleteUserById: User deleted successfully with ID: {}", id); // Log successful deletion
        } else {
            // Create a descriptive error message
            String errorMessage = "User with id " + id + " does not exist.";
            // Log a warning
            logger.warn("deleteUserById: " + errorMessage);
            // Handle the case when the user doesn't exist, e.g., log or throw an exception
            throw new RuntimeException(errorMessage); // Still throw the exception
        }
    }

}
