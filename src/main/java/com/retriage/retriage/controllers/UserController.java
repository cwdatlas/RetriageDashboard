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
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    /**
     *
     */
    private final UserService userService;

    /**
     * Constructor injection of the service
     */
    public UserController(UserService userService) {
        logger.info("Beginning User controller");
        logger.info("Starting User Service");
        this.userService = userService;
        logger.info("User Service started!");
    }

    /**
     * Creates a new User
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public String createUser(@Valid @RequestBody UserForm userForm) {
        logger.info("User Creation Request");
        //Secondary Validation done thru the GlobalException Handler

        User newUser = new User();
        newUser.setFirstName(userForm.getFirstName());
        newUser.setLastName(userForm.getLastName());
        newUser.setEmail(userForm.getEmail());
        newUser.setRole(userForm.getRole());
        newUser.setCreatedPatients(userForm.getCreatedPatients());
        logger.info("User Created");
        logger.info("Beginning saving user");
        User saved = userService.saveUser(newUser);
        logger.info("User Saved Successfully");
        return "User created: " + saved.getFirstName() + " " + saved.getLastName() + " " + saved.getEmail();

    }

    /**
     * Retrieves all Users from UserService
     */
    @GetMapping(produces = "application/json")
    public List<User> getAllUsers() {
        logger.info("Get ALl User Request");
        return userService.findAllUsers();
    }

    /**
     * Retrieve a single User based on their ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<User> findUserByID(@PathVariable Long id) {
        logger.info("Beginning findByID Request");
        Optional<User> optionalDirector = userService.findUserById(id);
        logger.info("User located successfully. Returning User information");
        return optionalDirector
                .map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific User based on their ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("User Deletion Request");
        userService.deleteUserById(id);
        logger.info("User Deletion Successful");
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing User based on their ID
     */
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        logger.info("User Update Request");
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            logger.info("User Update Failed. Could not find user with specified ID");
            return ResponseEntity.notFound().build();
        }
        logger.info("User updated successfully");
        return ResponseEntity.ok(updatedUser);
        //PUT is used for full updates, requires all fields, and
        //replaces the entire record with new data
    }

    /**
     * Patch or partially update a specified User given their ID
     */
    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("User Patch-Update Request");
        Optional<User> optionalUser = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            logger.info("Patch-Update Failed. Could not find user with specified ID");
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Apply updates dynamically
        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    user.setFirstName((String) value);
                    logger.info("New [FIRSTNAME] for User: " + user.getId(), " has been set.");
                    break;
                case "lastName":
                    user.setLastName((String) value);
                    logger.info("New [LASTNAME] for User: " + user.getId(), " has been set.");
                    break;
                case "email":
                    user.setEmail((String) value);
                    logger.info("New [EMAIL] for User: " + user.getId(), " has been set.");
                    break;
                case "role":
                    user.setRole((Role) value);
                    logger.info("New [ROLE] for User: " + user.getId(), " has been set.");
                    break;
            }
        });

        User updatedUser = userService.saveUser(user);
        logger.info("User updated successfully");
        return ResponseEntity.ok(updatedUser);
    }

}
