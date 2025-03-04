package com.retriage.retriage.services;
//1) Add logging to this; Done after or inside of the method
    //[Status] Method name: Error Message
    //Log statuses: Warning, Severe, Error, etc
//2) Build a fake user setup
//3) Throw errors

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.User; // Imports User model
import com.retriage.retriage.repositories.UserRepository; // Import UserRepository
import org.junit.jupiter.api.Test; // Import JUnit 5's Test annotation
import org.junit.jupiter.api.extension.ExtendWith; // Import JUnit 5's ExtendWith
import org.mockito.InjectMocks; // Imports Mockito's InjectMocks annotation
import org.mockito.Mock; // Imports Mockito's Mock annotation
import org.mockito.junit.jupiter.MockitoExtension; // Imports MockitoExtension


import java.util.List;

import static org.junit.jupiter.api.Assertions.*; // Static imports for assertions
import static org.mockito.Mockito.*; // Static imports for Mockito

@ExtendWith(MockitoExtension.class) // Enable Mockito for this test class
class UserServiceImpTest {
    @Mock // Create a mock instance of UserRepository
    private UserRepository userRepository;

    @InjectMocks // Inject the mocked UserRepository into UserServiceImp
    private UserServiceImp userServiceImp;

    // Helper method to create User objects for test setup
    private User createUser(Long id, String email, String lastName, Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName("User");
        user.setLastName(lastName);
        user.setRole(role);
        return user;
    }

    /**
     * deleteUser
     * INTENTIONALLY TRIGGERS ERROR LOG in deleteUser
     * This is to verify the logging in the deleteUser method when a user is not found.
     */
    @Test
    void triggerLogging_deleteUserNotFound() {
        // Arrange
        Long nonExistentUserId = 2L; // ID we assume doesn't exist (can be any ID not setup in mock)

        // Mock userRepository.existsById to return false, simulating user not found
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // Act
        userServiceImp.deleteUserById(nonExistentUserId);
    }

    /**
     * triggerLogging_UpdateUserNotFound
     * INTENTIONALLY TRIGGERS ERROR LOG in updateUser
     * This test is to verify the error logging in updateUser when the user is not found.
     */
    @Test
    void triggerLogging_UpdateUserNotFound() {
        // Arrange
        Long nonExistentUserId = 999L; // An ID that does not exist
        User userToUpdate = new User(); // We just need an object
        userToUpdate.setFirstName("UpdatedFirstName");

        // Mock the userRepository.existsById returning false, simulating a user not found
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // Act
        userServiceImp.updateUser(nonExistentUserId, userToUpdate);
        // No assertions needed on the result of updateUser for *this* logging test,
        // we are just interested in triggering the log output.
    }


}
