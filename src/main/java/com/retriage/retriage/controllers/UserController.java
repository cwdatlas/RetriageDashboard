package com.retriage.retriage.controllers;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.forms.UserForm;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {
    /**
     *
     */
    private final UserService userService;

    /**
     * Constructor injection of the service
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 1) Create a new User
     * POST /patients
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public String createUser(@Valid @RequestBody UserForm userForm) {
        //Secondary Validation

        User newUser = new User();
        newUser.setFirstName(userForm.getFirstName());
        newUser.setLastName(userForm.getLastName());
        newUser.setEmail(userForm.getEmail());
        newUser.setRole(userForm.getRole());
        newUser.setCreatedPatients(userForm.getCreatedPatients());
        User saved = userService.saveUser(newUser);
        return "User created: " + saved.getFirstName() + " " + saved.getLastName() + " " + saved.getEmail();
    }

    /**
     * 2) Get all Patients
     * GET /patients
     */
    @GetMapping(produces = "application/json")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    /**
     * 3) Get one Patient by ID
     * GET /patients/{id}
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<User> findUserByID(@PathVariable Long id) {
        Optional<User> optionalDirector = userService.findUserById(id);
        return optionalDirector
                .map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // OPTIONAL: Update or partial updates (PUT/PATCH) and Delete
    // For completeness, here's a simple delete example

    /**
     * 4) Delete a Patient
     * DELETE /patients/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 5) Update an existing User
     * PUT /user/{id}
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
     * 6) Partially Update a User
     * PATCH /user/{id}
     */
    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<User> optionalUser = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Apply updates dynamically
        updates.forEach((key, value) -> {
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
                    user.setRole((Role) value);
                    break;
            }
        });

        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }

}
