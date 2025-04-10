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
    private final JwtUtil jwtUtil;

    /**
     * UserServiceImp
     * User Service constructor
     *
     * @param userRepository Repository declared in UserServiceImp
     */
    public UserServiceImp(UserRepo userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
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
     * findAllUsers
     * Finds all currently saved User accounts
     *
     * @return Every user account
     */
    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("findAllUsers - Retrieved {} users.", users.size());
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
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("findUserById - User found with ID: {}", id);
        } else {
            logger.warn("findUserById - User find failed: No user found with ID: {}", id);
        }
        return user;
    }

    @Override
    public User getUserFromToken(String token) {
        logger.debug("getUserByToken - Token received: {}", token);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String email = jwtUtil.extractUsername(token); // assuming subject = email
        User user = getUserByEmail(email);

        if (user == null) {
            logger.warn("getUserFromToken - No user found for token subject: {}", email);
            throw new IllegalArgumentException("No user found for given token");
        }

        logger.info("getUserFromToken - User retrieved for token subject: {}", email);
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
        if (!userRepository.existsById(id)) {
            logger.warn("updateUser - User update failed: User with id {} not found.", id);
            return null;
        }
        try {
            validateUser(user);
            user.setId(id);
            User updatedUser = userRepository.save(user);
            logger.info("updateUser - User updated successfully with ID: {}", updatedUser.getId());
            return updatedUser;
        } catch (IllegalArgumentException e) {
            logger.warn("updateUser - User update failed: {}", e.getMessage());
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
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("deleteUserById - User deleted successfully with ID: {}", id);
        } else {
            logger.warn("deleteUserById - User delete failed: User with ID {} does not exist.", id);
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