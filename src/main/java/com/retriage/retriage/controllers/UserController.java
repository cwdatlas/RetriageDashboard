package com.retriage.retriage.controllers;

import com.retriage.retriage.forms.UserForm;
import com.retriage.retriage.models.User;
import com.retriage.retriage.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public String createUser(@RequestBody UserForm userForm) {
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
}
