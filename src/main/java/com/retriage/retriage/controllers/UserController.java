package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.forms.UserForm;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
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
        this.userService = userService;
    }

    /**
     * createUser
     * Creates a new User with the information passed in from userForm
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserForm userForm) {
        User newUser = new User();
        newUser.setFirstName(userForm.getFirstName());
        newUser.setLastName(userForm.getLastName());
        newUser.setEmail(userForm.getEmail());
        newUser.setRole(userForm.getRole());
        newUser.setCreatedPatients(userForm.getCreatedPatients());

        User saved = userService.saveUser(newUser);
        logger.info("createUser - User saved successfully with ID: {}", saved.getId());

        URI location = URI.create("/users/" + saved.getId());
        return ResponseEntity.created(location).body("User created: " + saved.getFirstName() + " " + saved.getLastName());
    }

    /**
     * getAllUsers
     * Retrieves all created Users from UserService
     */
    @GetMapping(produces = "application/json")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    /**
     * findUserByID
     * Retrieves a single User associated with the passed in ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<User> findUserByID(@PathVariable Long id) {
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
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * updateUser
     * Updates a User associated with the passed in ID
     */
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
        //PUT is used for full updates, requires all fields, and
        //replaces the entire record with new data
    }

    /**
     * patchUser
     * Patch or partially update a User associated with a specified ID
     */
    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<User> optionalUser = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(List.of("User with id " + id + " not found."), HttpStatus.NOT_FOUND.value(), "USER_NOT_FOUND"));
        }

        User user = optionalUser.get();
        List<String> errors = new ArrayList<>();

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "firstName":
                        user.setFirstName((String) value);
                        break;
                    case "lastName":
                        user.setLastName((String) value);
                        break;
                    case "email":
                        user.setEmail((String) value);
                        break;
                    case "role":
                        user.setRole(Role.valueOf((String) value));
                        break;
                    default:
                        errors.add("Invalid field provided for update: " + key);
                }
            } catch (IllegalArgumentException e) {
                errors.add("Invalid value for field " + key + ": " + value);
            }
        });

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(errors, HttpStatus.BAD_REQUEST.value(), "INVALID_INPUT"));
        }

        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }

}