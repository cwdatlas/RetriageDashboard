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
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*; // Static imports for assertions
import static org.mockito.Mockito.*; // Static imports for Mockito

@SpringBootTest
@ExtendWith(MockitoExtension.class) // Enable Mockito for this test class
class UserServiceImpTest {
    @Mock // Create a mock instance of UserRepository
    private UserRepository userRepository;

    @InjectMocks // Inject the mocked UserRepository into UserServiceImp
    private UserServiceImp userServiceImp;

    @TestConfiguration // Add this inner @TestConfiguration class
    static class TestConfig {
        @Bean  // Define a Validator bean
        public LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    // Helper method to create User objects for test setup
    private User createUser(Long id, String email, String firstname, String lastName, Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstname);
        user.setLastName(lastName);
        user.setRole(role);
        return user;
    }

    //Trigger Logging TESTS
    /**
     * Triggers and verifies the warning log in {@link UserServiceImp#deleteUserById(Long)}
     * when attempting to delete a user that does not exist.
     *
     * <p>
     * This is not a functional test of {@code deleteUserById} behavior, but rather a test
     * to ensure that the logging mechanism is correctly configured and that the
     * warning log message is generated under the expected condition (user not found).
     * </p>
     * <p>
     * It mocks {@code existsById(Long)} to return {@code false}
     * to simulate a non-existent user and force the execution path that contains the
     * {@code logger.warn} statement.
     * </p>
     * <p>
     * **Verification is manual:** After running this test, you should manually check the
     * console or log output to confirm that the warning log message is present
     * and in the expected format.
     * </p>
     */
    @Test
    void triggerLogging_deleteUserNotFound() {
        // Arrange
        Long nonExistentUserId = 2L; // ID we assume doesn't exist (can be any ID not setup in mock)

        // Mock userRepository.existsById to return false, simulating user not found
        when(userRepository.existsById(nonExistentUserId)).thenReturn(false);

        // Act & Assert (Use assertThrows to expect the RuntimeException)
        RuntimeException exception = assertThrows( // Assign the thrown exception to a variable (optional)
                RuntimeException.class, // Specify the EXPECTED exception type: RuntimeException
                () -> userServiceImp.deleteUserById(nonExistentUserId), // Lambda that calls the method that should throw the exception
                "Expected RuntimeException to be thrown when deleting non-existent user" // Optional failure message
        );
    }

    /**
     * Triggers and verifies the error log in {@link UserServiceImp#updateUser(Long, User)}
     * when attempting to update a user that does not exist.
     *
     * <p>
     * Similar to {@code triggerLogging_DeleteUserNotFound()}, this is not a functional test
     * but a test to verify the error logging for the "user not found" scenario in {@code updateUser}.
     * </p>
     * <p>
     * It mocks {@code UserRepository.existsById(Long)} to return {@code false}
     * to simulate a non-existent user and force the execution path with the
     * {@code logger.error} statement.
     * </p>
     * <p>
     * **Verification is manual:** After running this test, you should manually check the
     * console or log output to confirm that the error log message is present
     * and in the expected format.
     * </p>
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


    //saveUser TESTS
    /**
     * Tests the {@link UserServiceImp#saveUser(User)} method
     * when a valid user is provided.
     *
     * <p>
     * This test case verifies that:
     * <ul>
     *     <li>The {@code saveUser} method successfully saves the user.</li>
     *     <li>The method returns the saved User object.</li>
     *     <li>The returned User object contains the expected data (ID, first name, last name, email, role).</li>
     *     <li>The save method is called exactly once
     *         to persist the user in the database (mocked).</li>
     * </ul>
     * <p>
     * It mocks the save(User) method to return a pre-defined {@link User} object
     * to simulate the database interaction and isolate the {@code UserServiceImp} logic.
     */
    @Test
    void saveUser_ShouldReturnSavedUser() {
        // Sets up the test data and mock behavior
        User userToSave = new User(); // Create a User object to save
        userToSave.setId(3L);
        userToSave.setEmail("test@example.com");
        userToSave.setFirstName("Bob");
        userToSave.setLastName("Bobbert");
        userToSave.setRole(Role.Guest);

        User savedUser = new User(); // Creates a User object to represent what UserRepository.save would return
        savedUser.setId(1L); // Simulates the ID being generated by the database
        savedUser.setFirstName("Bob");
        savedUser.setLastName("Bobbert");
        savedUser.setEmail("test@example.com");
        savedUser.setRole(Role.Guest);

        // Mock the behavior of userRepository.save()
        when(userRepository.save(userToSave)).thenReturn(savedUser); // When save is called with userToSave, return savedUser

        // Act (Call the method we are testing)
        User result = userServiceImp.saveUser(userToSave);

        // Assert (Verify the results)
        assertNotNull(result); // Check that the result is not null
        assertEquals(savedUser.getId(), result.getId()); // Check if IDs match
        assertEquals(savedUser.getFirstName(), result.getFirstName()); // Check if first names match
        assertEquals(savedUser.getLastName(), result.getLastName()); // Check if the last names match
        assertEquals(savedUser.getRole(), result.getRole()); // Check if the roles match
        assertEquals(savedUser.getEmail(), result.getEmail()); // Check if emails match

        // Verify that userRepository.save() was called exactly once
        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void saveUser_NullRole_ShouldNotSaveUser() {
        // Arrange
        User invalidUser = createUser(4L, "test@example.com", "Test", "User", null); // Null role

        // Act
        userServiceImp.saveUser(invalidUser); // Call saveUser with invalid user - We are now intentionally ignoring the return value

        // Assert
        verify(userRepository, never()).save(any(User.class)); // Verify userRepository.save() is NEVER called
    }

    @Test
    void saveUser_InvalidEmail_ShouldThrowException() {
        // Arrange
        User invalidUser = createUser(4L, null, "Test", "User", Role.Guest); // Invalid email format

        // Act & Assert
        jakarta.validation.ConstraintViolationException exception = assertThrows(
                jakarta.validation.ConstraintViolationException.class, // Expect this exception to be thrown
                () -> userServiceImp.saveUser(invalidUser), // Lambda expression calling saveUser with invalid user
                "Expected ConstraintViolationException to be thrown for invalid email" // Optional message for assertion failure
        );
    }

    @Test
    void saveUser_BlankFirstName_ShouldThrowException() {
        // Arrange
        User invalidUser = createUser(54L, "test@example.com", "", "User", Role.Guest); // Blank first name

        // Act & Assert
        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userServiceImp.saveUser(invalidUser),
                "Expected ConstraintViolationException for blank first name");
    }

    //findAllUsers TESTS
    /**
     * Tests the {@link UserServiceImp#findAllUsers()} method
     * when users exist in the database (mocked).
     *
     * <p>
     * This test case verifies that:
     * <ul>
     *     <li>The {@code findAllUsers} method returns a list of {@link User} objects.</li>
     *     <li>The returned list is not null and contains the expected number of users.</li>
     *     <li>The users in the returned list have the expected properties (IDs and other details).</li>
     *     <li>The {@link UserRepository#findAll()} method is called exactly once to retrieve users
     *         from the database (mocked).</li>
     * </ul>
     * <p>
     * It mocks the {@link UserRepository#findAll()} method to return a pre-defined list of {@link User} objects
     * to simulate the database returning existing users.
     */
    @Test
    void findAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<User> mockUsers = List.of( // Create a list of mock User objects
                createUser(1L, "user1@example.com", "User","One", Role.Guest),
                createUser(2L, "user2@example.com", "User","Two", Role.Director)
        );
        when(userRepository.findAll()).thenReturn(mockUsers); // Mock userRepository.findAll() to return mockUsers

        // Act
        List<User> result = userServiceImp.findAllUsers();

        // Assert
        assertNotNull(result); // Check that the result is not null
        assertEquals(2, result.size()); // Check that the list size is correct
        assertEquals(mockUsers.get(0).getId(), result.get(0).getId()); // Check properties of the first user
        assertEquals(mockUsers.get(1).getId(), result.get(1).getId()); // Check properties of the second user
        // You could add more assertions to check other properties if needed

        verify(userRepository, times(1)).findAll(); // Verify userRepository.findAll() was called once
    }

    /**
     * Tests the {@link UserServiceImp#findAllUsers()} method
     * when no users exist in the database (mocked).
     *
     * <p>
     * This test case verifies that:
     * <ul>
     *     <li>The {@code findAllUsers} method returns a list of {@link User} objects.</li>
     *     <li>The returned list is not null but is empty, indicating no users were found.</li>
     *     <li>The {@link UserRepository#findAll()} method is called exactly once
     *         to retrieve users from the database (mocked).</li>
     * </ul>
     * <p>
     * It mocks the {@link UserRepository#findAll()} method to return an empty list
     * to simulate the database having no users.
     */
    @Test
    void findAllUsers_NoUsersExist_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of()); // Mock userRepository.findAll() to return an empty list

        // Act
        List<User> result = userServiceImp.findAllUsers();

        // Assert
        assertNotNull(result); // Result should not be null (it's a list)
        assertTrue(result.isEmpty()); // Result list should be empty
        verify(userRepository, times(1)).findAll();
    }

    //findUserById TESTS
    /**
     * Tests the {@link UserServiceImp#findUserById(Long)} method
     * when a user with the given ID exists in the database (mocked).
     *
     * <p>
     * This test case verifies that:
     * <ul>
     *     <li>The {@code findUserById} method returns an {@link Optional} containing a {@link User}.</li>
     *     <li>The returned {@link Optional} is not empty ({@code isPresent()} returns {@code true}).</li>
     *     <li>The {@link User} object within the {@link Optional} has the expected ID and other properties.</li>
     *     <li>The {@code UserRepository.findById(Long)} method is called exactly once with the correct ID
     *         to retrieve the user from the database (mocked).</li>
     * </ul>
     * <p>
     * It mocks the {@code UserRepository.findById(Long)} method to return an {@link Optional} containing a pre-defined {@link User} object
     * to simulate the database finding a user with the given ID.
     */
    @Test
    void findUserById_UserExists_ShouldReturnUserOptional() {
        // Arrange
        Long userId = 123L; // Example user ID
        User mockUser = createUser(userId, "existing.user@example.com", "TestFirstName","TestLastName", Role.Guest); // Create a mock User

        // Mock userRepository.findById(userId) to return an Optional containing mockUser
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        Optional<User> resultOptional = userServiceImp.findUserById(userId);

        // Assert
        assertNotNull(resultOptional); // Check if the returned Optional is not null
        assertTrue(resultOptional.isPresent()); // Check if the Optional contains a User (user was found)
        User resultUser = resultOptional.get(); // Get the User from the Optional (since we know it's present)
        assertEquals(userId, resultUser.getId()); // Check if the ID of the returned user is correct
        assertEquals(mockUser.getEmail(), resultUser.getEmail()); // Check other properties (optional, but good practice)
        assertEquals(mockUser.getFirstName(), resultUser.getFirstName());
        assertEquals(mockUser.getLastName(), resultUser.getLastName());
        assertEquals(mockUser.getRole(), resultUser.getRole());

        verify(userRepository, times(1)).findById(userId); // Verify userRepository.findById was called with the correct ID
    }

    /**
     * Tests the {@link UserServiceImp#findUserById(Long)} method
     * when no user with the given ID exists in the database (mocked).
     *
     * <p>
     * This test case verifies that:
     * <ul>
     *     <li>The {@code findUserById} method returns an empty {@link Optional}.</li>
     *     <li>The returned {@link Optional} is empty ({@code isPresent()} returns {@code false}).</li>
     *     <li>The {@code UserRepository.findById(Long)} method is called exactly once with the correct ID
     *         to attempt to retrieve the user from the database (mocked).</li>
     * </ul>
     * <p>
     * It mocks the {@code UserRepository.findById(Long)} method to return {@link Optional#empty()}
     * to simulate the database not finding a user with the given ID.
     */
    @Test
    void findUserById_UserNotFound_ShouldReturnEmptyOptional() {
        // Arrange
        Long userId = 456L; // Example user ID for a non-existent user

        // Mock userRepository.findById(userId) to return Optional.empty()
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> resultOptional = userServiceImp.findUserById(userId);

        // Assert
        assertNotNull(resultOptional); // Check if the returned Optional is not null
        assertTrue(resultOptional.isEmpty()); // Check if the Optional is empty (user not found)

        verify(userRepository, times(1)).findById(userId); // Verify userRepository.findById was called with the correct ID
    }

    //updateUser TESTS
    /**
     * Tests the {@link UserServiceImp#updateUser(Long, User)} method
     * when a valid user ID and valid updated user data are provided,
     * and the user exists in the database (mocked).
     *
     * <p>
     * This test case verifies that:
     * <ul>
     *     <li>The {@code updateUser} method successfully updates the user.</li>
     *     <li>The method returns the updated {@link User} object.</li>
     *     <li>The returned {@link User} object contains the updated data.</li>
     *     <li>The {@code UserRepository.existsById(Long)} method is called exactly once
     *         to check for user existence (mocked).</li>
     *     <li>The {@code UserRepository.save(User)} method is called exactly once
     *         to persist the updated user in the database (mocked).</li>
     * </ul>
     * <p>
     * It mocks {@code UserRepository.existsById(Long)} to return {@code true} and
     * {@code UserRepository.save(User)} to return the {@code updatedUser} object
     * to simulate a successful database update.
     */
    @Test
    void updateUser_SuccessfulUpdate_ShouldReturnUpdatedUser() {
        // Arrange
        Long userId = 123L;
        User existingUser = createUser(userId, "original.email@example.com", "Original", "User", Role.Guest);
        User updatedUser = createUser(userId, "updated.email@example.com", "Updated", "User", Role.Director);
        updatedUser.setFirstName("NewFirstName"); // Modify more properties if needed
        updatedUser.setLastName("NewLastName");

        when(userRepository.existsById(userId)).thenReturn(true); // Mock user exists
        when(userRepository.save(updatedUser)).thenReturn(updatedUser); // Mock successful save

        // Act
        User result = userServiceImp.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(updatedUser.getId(), result.getId());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        assertEquals(updatedUser.getFirstName(), result.getFirstName());
        assertEquals(updatedUser.getLastName(), result.getLastName());
        assertEquals(updatedUser.getRole(), result.getRole());

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).save(updatedUser);
    }

    /**
     * Tests the {@link UserServiceImp#updateUser(Long, User)} method
     * when a user with the given ID does NOT exist in the database (mocked).
     *
     * <p>
     * This test case verifies that:
     * <ul>
     *     <li>The {@code updateUser} method returns {@code null} when the user is not found.</li>
     *     <li>The {@code UserRepository.existsById(Long)} method is called exactly once
     *         to check for user existence (mocked).</li>
     *     <li>The {@code UserRepository.save(User)} method is NEVER called,
     *         as no update should be attempted for a non-existent user.</li>
     * </ul>
     * <p>
     * It mocks {@code UserRepository.existsById(Long)} to return {@code false}
     * to simulate the database not finding a user with the given ID.
     */
    @Test
    void updateUser_UserNotFound_ShouldReturnNull() {
        // Arrange
        Long userId = 456L; // ID for a non-existent user
        User updatedUser = createUser(userId, "updated.email@example.com", "Updated", "User", Role.Director);
        // We still create an updatedUser object, but it won't be used for saving in this scenario

        when(userRepository.existsById(userId)).thenReturn(false); // Mock user NOT exists

        // Act
        User result = userServiceImp.updateUser(userId, updatedUser);

        // Assert
        assertNull(result); // Expect null to be returned when user not found

        verify(userRepository, times(1)).existsById(userId); // Verify existsById was called
        verify(userRepository, never()).save(any(User.class)); // Verify save was NEVER called
    }

    /**
     * Tests the {@link UserServiceImp#updateUser(Long, User)} method
     * when the updated user data contains an invalid email format,
     * and expects a {@link jakarta.validation.ConstraintViolationException} to be thrown.
     *
     * <p>
     * This test verifies that:
     * <ul>
     *     <li>The {@code updateUser} method throws a {@link jakarta.validation.ConstraintViolationException}
     *         when validation of the updated user data fails due to an invalid email.</li>
     *     <li>Validation is enforced when updating a user.</li>
     *     <li>No user is saved or updated in the database (mocked).</li>
     * </ul>
     */
    @Test
    void updateUser_InvalidEmail_ShouldThrowException() {
        // Arrange
        Long userId = 123L;
        User invalidUser = createUser(userId, "invalid-email-format", "Test", "User", Role.Guest); // Invalid email

        when(userRepository.existsById(userId)).thenReturn(true); // Assume user exists for validation test

        // Act & Assert
        jakarta.validation.ConstraintViolationException exception = assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userServiceImp.updateUser(userId, invalidUser),
                "Expected ConstraintViolationException for invalid email in updateUser"
        );

        // Verify that userRepository.save is NOT called because of validation failure
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Tests the {@link UserServiceImp#updateUser(Long, User)} method
     * when the updated user data contains a blank first name,
     * and expects a {@link jakarta.validation.ConstraintViolationException} to be thrown.
     *
     * <p>
     * This test verifies that:
     * <ul>
     *     <li>The {@code updateUser} method throws a {@link jakarta.validation.ConstraintViolationException}
     *         when validation fails due to a blank first name.</li>
     *     <li>Validation is enforced for the first name field during user update.</li>
     *     <li>No user is saved or updated in the database (mocked).</li>
     * </ul>
     */
    @Test
    void updateUser_BlankFirstName_ShouldThrowException() {
        // Arrange
        Long userId = 123L;
        User invalidUser = createUser(userId, "test@example.com", "  ", "User", Role.Guest); // Blank first name

        when(userRepository.existsById(userId)).thenReturn(true); // Assume user exists for validation test

        // Act & Assert
        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userServiceImp.updateUser(userId, invalidUser),
                "Expected ConstraintViolationException for blank first name in updateUser");

        verify(userRepository, never()).save(any(User.class)); // Verify save is not called
    }

    /**
     * Tests the {@link UserServiceImp#updateUser(Long, User)} method
     * when the updated user data contains a null role,
     * and expects a {@link jakarta.validation.ConstraintViolationException} to be thrown.
     *
     * <p>
     * This test verifies that:
     * <ul>
     *     <li>The {@code updateUser} method throws a {@link jakarta.validation.ConstraintViolationException}
     *         when validation fails due to a null role.</li>
     *     <li>Validation is enforced for the role field during user update.</li>
     *     <li>No user is saved or updated in the database (mocked).</li>
     * </ul>
     */
    @Test
    void updateUser_NullRole_ShouldThrowException() {
        // Arrange
        Long userId = 123L;
        User invalidUser = createUser(userId, "test@example.com", "Test", null, null); // Null role

        when(userRepository.existsById(userId)).thenReturn(true); // Assume user exists for validation test

        // Act & Assert
        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> userServiceImp.updateUser(userId, invalidUser),
                "Expected ConstraintViolationException for null role in updateUser");

        verify(userRepository, never()).save(any(User.class)); // Verify save is not called
    }


}