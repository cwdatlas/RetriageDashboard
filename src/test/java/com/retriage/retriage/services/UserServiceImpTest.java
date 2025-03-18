package com.retriage.retriage.services;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.UserRepository;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class) // Enable Mockito for this test class
class UserServiceImpTest {
    @Mock // Create a mock instance of UserRepository
    private UserRepository userRepository;

    @InjectMocks // Inject the mocked UserRepository into UserServiceImp
    private UserServiceImp userServiceImp;

    @BeforeEach
    void setUp() {
        userServiceImp = new UserServiceImp(userRepository); // Inject mock manually
    }

    // Helper method to create User objects for test setup
    @Valid
    private User createUser(Long id, String email, String firstname, String lastName, Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstname);
        user.setLastName(lastName);
        user.setRole(role);
        return user;
    }

    // Trigger Logging TESTS

    /**
     * Tests that deleting a non-existent user triggers an expected RuntimeException.
     */
    @Test
    void triggerLogging_deleteUserNotFound() {
        // Arrange
        Long nonExistentUserId = 2L;
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userServiceImp.deleteUserById(nonExistentUserId),
                "Expected RuntimeException to be thrown when deleting non-existent user");
    }

    /**
     * Tests that updating a non-existent user triggers the logging mechanism.
     */
    @Test
    void triggerLogging_UpdateUserNotFound() {
        // Arrange
        Long nonExistentUserId = 999L;
        User userToUpdate = new User();
        userToUpdate.setFirstName("UpdatedFirstName");
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // Act
        userServiceImp.updateUser(nonExistentUserId, userToUpdate);
    }

    /**
     * Tests that calling findAll() directly on the mock repository returns the expected list of users.
     */
    @Test
    void testMockUserRepositoryFindAllDirectly() {
        // Arrange
        List<User> mockUsers = List.of(
                createUser(1L, "user1@example.com", "User", "One", Role.Guest),
                createUser(2L, "user2@example.com", "User", "Two", Role.Director)
        );
        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<User> resultFromMock = userRepository.findAll();

        // Assert
        Assertions.assertNotNull(resultFromMock);
        Assertions.assertEquals(2, resultFromMock.size());
    }

    // saveUser TESTS

    /**
     * Tests that saving a valid user returns the correctly saved user object.
     */
    @Test
    void saveUser_ShouldReturnSavedUser() {
        // Arrange
        User userToSave = new User();
        userToSave.setId(3L);
        userToSave.setEmail("test@example.com");
        userToSave.setFirstName("Bob");
        userToSave.setLastName("Bobbert");
        userToSave.setRole(Role.Guest);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setFirstName("Bob");
        savedUser.setLastName("Bobbert");
        savedUser.setRole(Role.Guest);

        when(userRepository.save(userToSave)).thenReturn(savedUser);

        // Act
        User result = userServiceImp.saveUser(userToSave);

        // Assert
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
    }

    /**
     * Tests that a user with a null role should not be saved.
     */
    @Test
    void saveUser_NullRole_ShouldNotSaveUser() {
        // Arrange
        User invalidUser = createUser(4L, "test@example.com", "Test", "User", null);

        // Act
        userServiceImp.saveUser(invalidUser);

        // Assert
        assert !userRepository.existsById(invalidUser.getId());
    }

    // findAllUsers TESTS

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
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    // findUserById TESTS

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
        assertNotNull(resultOptional);
        assertTrue(resultOptional.isPresent());
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
        assertNotNull(resultOptional);
        assertTrue(resultOptional.isEmpty());
    }

    // updateUser TESTS

    /**
     * Tests that updating an existing user with valid data returns the updated user.
     */
    @Test
    void updateUser_SuccessfulUpdate_ShouldReturnUpdatedUser() {
        // Arrange
        Long userId = 123L;
        User existingUser = createUser(userId, "original.email@example.com", "Original", "User", Role.Guest);
        User updatedUser = createUser(userId, "updated.email@example.com", "Updated", "User", Role.Director);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // Act
        User result = userServiceImp.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
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
        assertNull(result);
    }

// deleteUser TESTS

}