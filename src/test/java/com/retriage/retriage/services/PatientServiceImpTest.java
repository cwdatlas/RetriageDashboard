package com.retriage.retriage.services;

import com.retriage.retriage.enums.Condition;
import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.Patient;
import com.retriage.retriage.models.User;
import com.retriage.retriage.repositories.PatientRepository;
import org.junit.jupiter.api.Test; // Import Test annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; // Static imports for assertions
import static org.mockito.Mockito.*; // Static imports for Mockito

@SpringBootTest
public class PatientServiceImpTest {
    @MockitoBean
    private PatientRepository patientRepository;
    @Autowired
    private PatientServiceImp patientServiceImp;

    /**
     * Helper method to create a Patient for test purposes.
     */
    private Patient createPatient(Long id, String cardId, String firstName, String lastName, Condition condition, User retriageNurse) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setCardId(cardId);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setCondition(condition);
        patient.setRetriageNurse(retriageNurse);
        return patient;
    }

    // ==================== savePatient TESTS ====================
    /**
     * Tests that saving a valid patient returns the correctly saved patient object.
     */
    @Test
    void savePatient_ShouldReturnSavedPatient() {
        // Arrange
        User mockNurse = new User(); // Mock User, details don't matter for this test
        Patient patientToSave = createPatient(null, "12345", "Test", "Patient", Condition.Green, mockNurse); // ID is null for new patient
        Patient savedPatient = createPatient(1L, "12345", "Test", "Patient", Condition.Green, mockNurse); // ID is now 1L, as if assigned by DB

        when(patientRepository.save(patientToSave)).thenReturn(savedPatient); // Mock repository save behavior

        // Act
        Patient result = patientServiceImp.savePatient(patientToSave);

        // Assert
        assertNotNull(result, "Saved patient should not be null");
        assertEquals(savedPatient.getId(), result.getId(), "Saved patient ID should match");
        assertEquals(savedPatient.getCardId(), result.getCardId(), "Saved patient Card ID should match");
        assertEquals(savedPatient.getFirstName(), result.getFirstName(), "Saved patient First Name should match");
        // ... You can add assertions for other fields as needed
        verify(patientRepository, times(1)).save(patientToSave); // Verify repository save was called once
    }

    /**
     * Tests that saving a patient with a non-nurse retriage nurse throws an exception.
     */
    @Test
    void savePatient_NonNurseRetriageNurse_ShouldThrowException() {
        // Arrange
        User nonNurseUser = new User(); // User with default role (which is not Nurse)
        nonNurseUser.setRole(Role.Guest); // Explicitly set to Guest role for clarity
        Patient patientToSave = createPatient(null, "12345", "Test", "Patient", Condition.Green, nonNurseUser);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> patientServiceImp.savePatient(patientToSave),
                "Expected IllegalArgumentException for non-nurse retriage nurse");

        // Verify that save() was never called on the repository because validation failed
        verify(patientRepository, never()).save(any(Patient.class));
    }

    /**
     * Tests that saving a patient with a nurse retriage nurse succeeds.
     */
    @Test
    void savePatient_NurseRetriageNurse_ShouldSavePatient() {
        // Arrange
        User nurseUser = new User();
        nurseUser.setRole(Role.Nurse); // Set the role to Nurse
        Patient patientToSave = createPatient(null, "12345", "Test", "Patient", Condition.Expectant, nurseUser);
        Patient savedPatient = createPatient(1L, "12345", "Test", "Patient", Condition.Expectant, nurseUser);
        when(patientRepository.save(patientToSave)).thenReturn(savedPatient);

        // Act
        Patient result = patientServiceImp.savePatient(patientToSave);

        // Assert
        assertNotNull(result, "Saved patient should not be null");
        assertEquals(savedPatient.getId(), result.getId(), "Saved patient ID should match");
        // ... other assertions if needed
        verify(patientRepository, times(1)).save(patientToSave); // Verify repository save was called
    }

    /**
     * Edge Case Test: Tests saving a patient that already has an ID set.
     * Behavior: Should still attempt to save (potentially update if ID exists in DB, or insert) without error.
     */
    @Test
    void savePatient_PatientWithExistingId_ShouldSaveWithoutError() {
        // Arrange
        User mockNurse = new User();
        mockNurse.setRole(Role.Nurse);
        Patient patientToSave = createPatient(
                10L,
                "ExistingIDCard",
                "ExistingID", "Patient",
                Condition.Red, mockNurse); // Patient with ID 10 already set
        Patient savedPatient = createPatient(
                10L,
                "ExistingIDCard",
                "ExistingID", "Patient",
                Condition.Red, mockNurse); // Mock saved patient

        when(patientRepository.save(patientToSave)).thenReturn(savedPatient); // Mock repository save

        // Act
        Patient result = patientServiceImp.savePatient(patientToSave);

        // Assert
        assertNotNull(result, "Saved patient should not be null");
        assertEquals(patientToSave.getId(), result.getId(), "Saved patient ID should match the existing ID"); // Verify ID is preserved
        verify(patientRepository, times(1)).save(patientToSave); // Verify repository save was called
    }

    /**
     * Edge Case Test: Tests saving a patient with a null resourceList.
     * Behavior: Should save successfully as resourceList is optional.
     */
    @Test
    void savePatient_NullResourceList_ShouldSaveSuccessfully() {
        // Arrange
        User mockNurse = new User();
        mockNurse.setRole(Role.Nurse);
        Patient patientToSave = createPatient(
                null,
                "NullResourceCard",
                "NullResource", "Patient",
                Condition.Green, mockNurse);
        patientToSave.setResourceList(null); // Set resourceList to null
        Patient savedPatient = createPatient(
                11L,
                "NullResourceCard",
                "NullResource", "Patient",
                Condition.Green, mockNurse);
        savedPatient.setResourceList(null);

        when(patientRepository.save(patientToSave)).thenReturn(savedPatient);

        // Act
        Patient result = patientServiceImp.savePatient(patientToSave);

        // Assert
        assertNotNull(result, "Saved patient should not be null");
        assertNull(result.getResourceList(), "ResourceList should be null in saved patient");
        verify(patientRepository, times(1)).save(patientToSave);
    }

    /**
     * Edge Case Test: Tests saving a patient with an empty resourceList.
     * Behavior: Should save successfully with an empty resourceList.
     */
    @Test
    void savePatient_EmptyResourceList_ShouldSaveSuccessfully() {
        // Arrange
        User mockNurse = new User();
        mockNurse.setRole(Role.Nurse);
        Patient patientToSave = createPatient(
                null,
                "EmptyResourceCard",
                "EmptyResource", "Patient",
                Condition.Green, mockNurse);
        patientToSave.setResourceList(Collections.emptyList()); // Set resourceList to empty list
        Patient savedPatient = createPatient(
                12L,
                "EmptyResourceCard",
                "EmptyResource", "Patient",
                Condition.Green, mockNurse);
        savedPatient.setResourceList(Collections.emptyList());

        when(patientRepository.save(patientToSave)).thenReturn(savedPatient);

        // Act
        Patient result = patientServiceImp.savePatient(patientToSave);

        // Assert
        assertNotNull(result, "Saved patient should not be null");
        assertTrue(result.getResourceList().isEmpty(), "ResourceList should be empty in saved patient");
        verify(patientRepository, times(1)).save(patientToSave);
    }

    // ==================== getAllPatients TESTS ====================
    /**
     * Tests that getAllPatients() returns a list of existing patients.
     */
    @Test
    void getAllPatients_ShouldReturnListOfPatients() {
        // Arrange
        User mockNurse = new User();
        List<Patient> mockPatients = List.of(
                createPatient(1L, "111", "Patient", "One", Condition.Red, mockNurse),
                createPatient(2L, "222", "Patient", "Two", Condition.Green, mockNurse)
        );
        when(patientRepository.findAll()).thenReturn(mockPatients); // Mock findAll to return mockPatients

        // Act
        List<Patient> result = patientServiceImp.getAllPatients();

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(2, result.size(), "The result list should contain 2 patients");
        assertEquals(mockPatients.get(0).getId(), result.get(0).getId(), "First patient ID should match");
        assertEquals(mockPatients.get(1).getId(), result.get(1).getId(), "Second patient ID should match");
        verify(patientRepository, times(1)).findAll(); // Verify findAll was called once
    }

    /**
     * Tests that getAllPatients() returns an empty list when no patients exist.
     */
    @Test
    void getAllPatients_ShouldReturnEmptyListWhenNoPatients() {
        // Arrange
        when(patientRepository.findAll()).thenReturn(Collections.emptyList()); // Mock findAll to return empty list

        // Act
        List<Patient> result = patientServiceImp.getAllPatients();

        // Assert
        assertNotNull(result, "The result should not be null");
        assertTrue(result.isEmpty(), "The result list should be empty");
        verify(patientRepository, times(1)).findAll(); // Verify findAll was called once
    }

    // ==================== getPatientById TESTS ====================
    /**
     * Tests that getPatientById() returns the correct patient if the patient exists.
     */
    @Test
    void getPatientById_PatientExists_ShouldReturnPatientOptional() {
        // Arrange
        Long patientId = 123L;
        User mockNurse = new User(); //Empty user works due to isolated mock User
        Patient mockPatient = createPatient(patientId, "P123", "Existing", "Patient", Condition.Expectant, mockNurse);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(mockPatient)); // Mock findById to return mockPatient

        // Act
        Optional<Patient> resultOptional = patientServiceImp.getPatientById(patientId);

        // Assert
        assertTrue(resultOptional.isPresent(), "Expected a patient to be found");
        assertEquals(patientId, resultOptional.get().getId(), "The returned patient ID should match");
        assertEquals(mockPatient.getFirstName(), resultOptional.get().getFirstName(), "Returned first name should match");
        verify(patientRepository, times(1)).findById(patientId); // Verify findById was called once with patientId
    }

    /**
     * Tests that getPatientById() returns an empty Optional when the patient is not found.
     */
    @Test
    void getPatientById_PatientNotFound_ShouldReturnEmptyOptional() {
        // Arrange
        Long patientId = 456L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty()); // Mock findById to return empty Optional

        // Act
        Optional<Patient> resultOptional = patientServiceImp.getPatientById(patientId);

        // Assert
        assertTrue(resultOptional.isEmpty(), "Expected no patient to be found");
        verify(patientRepository, times(1)).findById(patientId); // Verify findById was called once with patientId
    }

    /**
     * Edge Case Test: Tests getPatientById() with a zero ID.
     * Behavior: Should return an empty Optional as no patient is expected with ID 0.
     */
    @Test
    void getPatientById_ZeroId_ShouldReturnEmptyOptional() {
        // Arrange
        Long patientId = 0L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty()); // Mock repository to return empty for ID 0

        // Act
        Optional<Patient> resultOptional = patientServiceImp.getPatientById(patientId);

        // Assert
        assertTrue(resultOptional.isEmpty(), "Expected no patient to be found for ID 0");
        verify(patientRepository, times(1)).findById(patientId);
    }

    /**
     * Edge Case Test: Tests getPatientById() with a negative ID.
     * Behavior: Should return an empty Optional as no patient is expected with negative ID.
     */
    @Test
    void getPatientById_NegativeId_ShouldReturnEmptyOptional() {
        // Arrange
        Long patientId = -1L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty()); // Mock repository to return empty for ID -1

        // Act
        Optional<Patient> resultOptional = patientServiceImp.getPatientById(patientId);

        // Assert
        assertTrue(resultOptional.isEmpty(), "Expected no patient to be found for negative ID");
        verify(patientRepository, times(1)).findById(patientId);
    }

    // ==================== deletePatient TESTS ====================
    /**
     * Tests that deletePatient() succeeds when the patient exists.
     */
    @Test
    void deletePatient_PatientExists_ShouldSucceed() {
        // Arrange
        Long patientId = 789L;
        when(patientRepository.existsById(patientId)).thenReturn(true); // Mock existsById to return true (patient exists)
        doNothing().when(patientRepository).deleteById(patientId); // Mock deleteById to do nothing (void method)

        // Act & Assert
        assertDoesNotThrow(() -> patientServiceImp.deletePatient(patientId), // Execute deletePatient and assert no exception
                "Deleting an existing patient should not throw an exception");

        verify(patientRepository, times(1)).existsById(patientId); // Verify existsById was called once
        verify(patientRepository, times(1)).deleteById(patientId); // Verify deleteById was called once
    }

    /**
     * Tests that deletePatient() throws IllegalArgumentException when the patient does not exist.
     */
    @Test
    void deletePatient_PatientNotFound_ShouldThrowException() {
        // Arrange
        Long nonExistentPatientId = 101L;
        when(patientRepository.existsById(nonExistentPatientId)).thenReturn(false); // Mock existsById to return false (patient not found)

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> patientServiceImp.deletePatient(nonExistentPatientId), // Execute deletePatient and assert exception
                "Expected IllegalArgumentException when deleting a non-existent patient");

        verify(patientRepository, times(1)).existsById(nonExistentPatientId); // Verify existsById was called once
        verify(patientRepository, never()).deleteById(anyLong()); // Verify deleteById was never called
    }

    // ==================== updatePatient TESTS ====================
    /**
     * Tests that updatePatient() successfully updates and returns the updated patient when valid data is provided
     * and the patient exists.
     */
    @Test
    void updatePatient_SuccessfulUpdate_ShouldReturnTrue() {
        // Arrange
        Long patientId = 123L;
        User mockNurse = new User();
        mockNurse.setRole(Role.Nurse);
        Patient existingPatient = createPatient(patientId, "P123", "Original", "Patient", Condition.Red, mockNurse);
        Patient updatedPatientData = createPatient(null, "P456", "Updated", "PatientName", Condition.Red, mockNurse); // ID is null as it's update data

        when(patientRepository.existsById(patientId)).thenReturn(true); // Mock existsById to return true (patient exists)
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatientData); // Mock save to return updatedPatientData

        // Act
        boolean result = patientServiceImp.updatePatient(patientId, updatedPatientData); // Changed to boolean result

        // Assert
        assertTrue(result, "Update should be successful and return true"); // Assert boolean result is true
        verify(patientRepository, times(1)).existsById(patientId); // Verify existsById was called
        verify(patientRepository, times(1)).save(any(Patient.class)); // Verify save was called
    }

    /**
     * Tests that updatePatient() returns null when attempting to update a non-existent patient.
     */
    @Test
    void updatePatient_PatientNotFound_ShouldReturnFalse() {
        // Arrange
        Long patientId = 456L;
        User mockNurse = new User();
        Patient updatedPatientData = createPatient(patientId, "P456", "Updated", "PatientName", Condition.Red, mockNurse); // ID set for clarity
        when(patientRepository.existsById(patientId)).thenReturn(false); // Mock existsById to return false (patient not found)

        // Act
        boolean result = patientServiceImp.updatePatient(patientId, updatedPatientData); // Changed to boolean result

        // Assert
        assertFalse(result, "Expected updatePatient to return false for non-existent patient"); // Assert boolean result is false
        verify(patientRepository, times(1)).existsById(patientId); // Verify existsById was called
        verify(patientRepository, never()).save(any(Patient.class)); // Verify save was not called
    }

    /**
     * Tests that updatePatient() throws IllegalArgumentException when provided patient data is invalid.
     */
    @Test
    void updatePatient_InvalidPatientData_ShouldThrowException() {
        // Arrange
        Long patientId = 789L;
        User mockNurse = new User();
        // Creating invalid patient data - e.g., missing first name
        Patient invalidPatientData = createPatient(null, "P789", null, "PatientName", Condition.Green, mockNurse);
        when(patientRepository.existsById(patientId)).thenReturn(true); // Mock existsById to return true (patient exists)

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> patientServiceImp.updatePatient(patientId, invalidPatientData),
                "Expected IllegalArgumentException for invalid patient data");
        verify(patientRepository, times(1)).existsById(patientId); // Verify existsById was called
        verify(patientRepository, never()).save(any(Patient.class)); // Verify save was not called, validation should prevent it
    }

    /**
     * Edge Case Test: Tests updatePatient() when no actual changes are made to patient data.
     * Behavior: Should still return true (successful update attempt) and call repository's save method.
     */
    @Test
    void updatePatient_NoActualChanges_ShouldReturnTrue() {
        // Arrange
        Long patientId = 123L;
        User mockNurse = new User();
        mockNurse.setRole(Role.Nurse);
        Patient existingPatient = createPatient(
                patientId,
                "P123",
                "Original", "Patient",
                Condition.Green, mockNurse);

        when(patientRepository.existsById(patientId)).thenReturn(true);
        when(patientRepository.save(existingPatient)).thenReturn(existingPatient); // Mock save to return the same existingPatient

        // Act
        boolean result = patientServiceImp.updatePatient(patientId, existingPatient); // Pass in existingPatient as update data

        // Assert
        assertTrue(result, "Update should be considered successful and return true");
        verify(patientRepository, times(1)).existsById(patientId);
        verify(patientRepository, times(1)).save(existingPatient); // Verify save is still called
    }
}
