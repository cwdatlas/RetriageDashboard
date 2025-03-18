package com.retriage.retriage.services;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImpTest {
    @Autowired
    private UserServiceImp userServiceImp; // Service under test

    @MockitoBean
    private UserRepo userRepository; // Repository is replaced with a mock

    /**
     * Helper method to create a User for test purposes.
     */
    private User createUser(Long id, String email, String firstName, String lastName, Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        return user;
    }

    // ==================== findAllUsers TESTS ====================

    /**
     * Tests that findAllUsers() returns a list of existing users.
     */
    @Test
    void findAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<User> mockUsers = List.of(
                createUser(1L, "user1@example.com", "User", "One", Role.Guest),
                createUser(2L, "user2@example.com", "User", "Two", Role.Director)
        );
        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<User> result = userServiceImp.findAllUsers();

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(2, result.size(), "The result list should contain 2 users");
        verify(userRepository, times(1)).findAll();
    }
// ==================== findUserById TESTS ====================

    /**
     * Tests that findUserById() returns the correct user if the user exists.
     */
    @Test
    void findUserById_UserExists_ShouldReturnUserOptional() {
        // Arrange
        Long userId = 123L;
        User mockUser = createUser(userId, "existing.user@example.com", "TestFirstName", "TestLastName", Role.Guest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> resultOptional = userServiceImp.findUserById(userId);

        // Assert
        assertTrue(resultOptional.isPresent(), "Expected a user to be found");
        assertEquals(userId, resultOptional.get().getId(), "The returned user ID should match");
        verify(userRepository, times(1)).findById(userId);
    }

    /**
     * Tests that findUserById() returns an empty Optional when the user is not found.
     */
    @Test
    void findUserById_UserNotFound_ShouldReturnEmptyOptional() {
        // Arrange
        Long userId = 456L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> resultOptional = userServiceImp.findUserById(userId);

        // Assert
        assertTrue(resultOptional.isEmpty(), "Expected no user to be found");
        verify(userRepository, times(1)).findById(userId);
    }

    // ==================== saveUser TESTS ====================

    /**
     * Tests that saving a valid user returns the correctly saved user object.
     */
    @Test
    void saveUser_ShouldReturnSavedUser() {
        // Arrange
        User userToSave = createUser(3L, "test@example.com", "Bob", "Bobbert", Role.Guest);
        User savedUser = createUser(3L, "test@example.com", "Bob", "Bobbert", Role.Guest);
        when(userRepository.save(userToSave)).thenReturn(savedUser);

        // Act
        User result = userServiceImp.saveUser(userToSave);

        // Assert
        assertNotNull(result, "Saved user should not be null");
        assertEquals(savedUser.getEmail(), result.getEmail(), "Emails should match");
        verify(userRepository, times(1)).save(userToSave);
    }

    /**
     * Tests that saving a user with a null role does not result in a repository save.
     */
    @Test
    void saveUser_NullRole_ShouldNotSaveUser() {
        // Arrange
        User invalidUser = createUser(4L, "test@example.com", "Test", "User", null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userServiceImp.saveUser(invalidUser));

        // Verify that save() was never called
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== updateUser TESTS ====================

    /**
     * Tests that updating an existing user with valid data returns the updated user.
     */
    @Test
    void updateUser_SuccessfulUpdate_ShouldReturnUpdatedUser() {
        // Arrange
        Long userId = 123L;
        User updatedUser = createUser(userId, "updated.email@example.com", "Updated", "User", Role.Director);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // Act
        User result = userServiceImp.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result, "Updated user should not be null");
        assertEquals("updated.email@example.com", result.getEmail(), "Email should be updated");
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).save(updatedUser);
    }

    /**
     * Tests that updating a non-existent user returns null.
     */
    @Test
    void updateUser_UserNotFound_ShouldReturnNull() {
        // Arrange
        Long userId = 456L;
        User updatedUser = createUser(userId, "updated.email@example.com", "Updated", "User", Role.Director);
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act
        User result = userServiceImp.updateUser(userId, updatedUser);

        // Assert
        assertNull(result, "Expected updateUser to return null for non-existent user");
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== deleteUser TESTS ====================

    /**
     * Tests that deleting an existing user does not throw an exception.
     */
    @Test
    void deleteUser_UserExists_ShouldSucceed() {
        // Arrange
        Long userId = 789L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // Act & Assert
        assertDoesNotThrow(() -> userServiceImp.deleteUserById(userId),
                "Deleting an existing user should not throw an exception");
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    /**
     * Tests that deleting a non-existent user throws a RuntimeException.
     */
    @Test
    void deleteUser_UserNotFound_ShouldThrowException() {
        // Arrange
        Long nonExistentUserId = 101L;
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userServiceImp.deleteUserById(nonExistentUserId),
                "Expected RuntimeException when deleting a non-existent user");
        verify(userRepository, times(1)).existsById(nonExistentUserId);
        verify(userRepository, never()).deleteById(anyLong());
    }

}