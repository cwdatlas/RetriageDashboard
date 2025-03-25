package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.forms.UserForm;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.EventServiceImp;
import com.retriage.retriage.services.UserService;
import com.retriage.retriage.services.UserServiceImp;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * Constructor injection of the service
     */
    public UserController(UserService userService) {
        logger.info("Entering UserController constructor with userService: {}", userService);
        this.userService = userService;
        logger.info("Exiting UserController constructor");
    }

    /**
     * createUser
     * Creates a new User with the information passed in from userForm
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public String createUser(@Valid @RequestBody UserForm userForm) {
        logger.info("Entering createUser with userForm: {}", userForm);
        //Secondary Validation done thru the GlobalException Handler

        User newUser = new User();
        newUser.setFirstName(userForm.getFirstName());
        newUser.setLastName(userForm.getLastName());
        newUser.setEmail(userForm.getEmail());
        newUser.setRole(userForm.getRole());
        newUser.setCreatedPatients(userForm.getCreatedPatients());
        logger.debug("createUser - Created User object from form: {}", newUser);
        User saved = userService.saveUser(newUser);
        logger.info("createUser - User saved successfully with ID: {}", saved.getId());
        String response = "User created: " + saved.getFirstName() + " " + saved.getLastName() + " " + saved.getEmail();
        logger.info("Exiting createUser, returning response: {}", response);
        return response;
    }

    /**
     * getAllUsers
     * Retrieves all created Users from UserService
     */
    @GetMapping(produces = "application/json")
    public List<User> getAllUsers() {
        logger.info("Entering getAllUsers");
        List<User> users = userService.findAllUsers();
        logger.info("Exiting getAllUsers, returning {} users", users.size());
        return users;
    }

    /**
     * findUserByID
     * Retrieves a single User associated with the passed in ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<User> findUserByID(@PathVariable Long id) {
        logger.info("Entering findUserByID with id: {}", id);
        Optional<User> optionalUser = userService.findUserById(id);
        ResponseEntity<User> response = optionalUser
                .map(user -> {
                    logger.info("findUserByID - Found user with id: {}", id);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("findUserByID - User with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
        logger.info("Exiting findUserByID, returning response: {}", response.getStatusCode());
        return response;
    }

    /**
     * deleteUser
     * Deletes a User specified by their passed in ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Entering deleteUser with id: {}", id);
        userService.deleteUserById(id);
        logger.info("Exiting deleteUser, user with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * updateUser
     * Updates a User associated with the passed in ID
     */
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        logger.info("Entering updateUser with id: {} and user: {}", id, user);
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            logger.warn("updateUser - User with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Exiting updateUser, user with id {} updated successfully", id);
        return ResponseEntity.ok(updatedUser);
        //PUT is used for full updates, requires all fields, and
        //replaces the entire record with new data
    }

    /**
     * patchUser
     * Patch or partially update a User associated with a specified ID
     */
    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("Entering patchUser with id: {} and updates: {}", id, updates);
        Optional<User> optionalUser = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            logger.warn("patchUser - User with id {} not found for patch update", id);
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        logger.debug("patchUser - Retrieved existing user: {}", user);

        // Apply updates dynamically
        updates.forEach((key, value) -> {
            logger.debug("patchUser - Applying update for field: {}, value: {}", key, value);
            switch (key) {
                case "firstName":
                    user.setFirstName((String) value);
                    logger.debug("patchUser - First name updated for user with ID: {}", user.getId());
                    break;
                case "lastName":
                    user.setLastName((String) value);
                    logger.debug("patchUser - Last name updated for user with ID: {}", user.getId());
                    break;
                case "email":
                    user.setEmail((String) value);
                    logger.debug("patchUser - Email updated for user with ID: {}", user.getId());
                    break;
                case "role":
                    user.setRole((Role) value);
                    logger.debug("patchUser - Role updated for user with ID: {}", user.getId());
                    break;
                default:
                    logger.warn("patchUser - Invalid field provided for update: {}", key);
            }
        });

        User updatedUser = userService.saveUser(user);
        logger.info("Exiting patchUser, user with id {} updated successfully", id);
        return ResponseEntity.ok(updatedUser);
    }

}