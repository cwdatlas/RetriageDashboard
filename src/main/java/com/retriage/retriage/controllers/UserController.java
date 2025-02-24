package com.retriage.retriage.controllers;

import com.retriage.retriage.models.User;
import com.retriage.retriage.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/resources")
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
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.saveUser(user);
        return ResponseEntity.
                created(URI.create("/usr/" + saved.getId()))
                .body(saved);
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
    @GetMapping(value = "/usr/{id}", produces = "application/json")
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
    @DeleteMapping("/usr/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * getAllActiveUsers()
     */
//    @GetMapping(produces = "application/json")
//    public List<User> getAllActiveUsers() {
//        return userService.findAllUsers();
//    }

    /**
     *  getAllNurses()
     */
//    @GetMapping(produces = "application/json")
//    public List<User> getAllNurses() {
//        return userService.findAllUsers();
//    }

    /**
     *  getAllDirectors()
     */
//    @GetMapping(produces = "application/json")
//    public List<User> getAllDirector() {
//        return userService.findAllUsers();
//    }

    /**
     *  getAllGuests()
     */
//    @GetMapping(produces = "application/json")
//    public List<User> getAllGuests() {
//        return userService.findAllUsers();
//    }


}
