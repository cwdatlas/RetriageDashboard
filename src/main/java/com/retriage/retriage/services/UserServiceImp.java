package com.retriage.retriage.services;

import java.util.List;
import java.util.Optional;

import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    /**
     * User Service constructor
     * @param userRepository Repository declared in UserServiceImp
     */
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Saves a user
     * @param user
     * @return
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Finds all currently saved User accounts
     * @return
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Finds a director via their ID
     * @param id The ID of the User you're looking for
     * @return The User object assigned to the passed in ID
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Remove a User from saved list.
     * @param id The ID of the director to be deleted
     */
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
