package com.retriage.retriage.services;

import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.repositories.PatientPoolRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; // Static import for assertions
import static org.mockito.Mockito.*; // Static import for Mockito


@SpringBootTest
public class ResourceServiceImpTest {

    @MockitoBean
    private PatientPoolRepo resourceRepository;

    @Autowired
    private PatientPoolServiceImp resourceServiceImp;

    /**
     * Helper method to create a PatientPool for test purposes.
     */
    private PatientPool createResource(Long id, String name, int processTime, boolean active, boolean useable) {
        PatientPool resource = new PatientPool();
        resource.setId(id);
        resource.setName(name);
        resource.setProcessTime(processTime);
        resource.setActive(active);
        resource.setUseable(useable);
        return resource;
    }

    // ==================== saveResource TESTS ====================
    /**
     * Tests that saveResource() returns true when a resource is successfully saved.
     */
    @Test
    void saveResource_ShouldReturnTrueWhenSaveSuccessful() {
        // Arrange
        PatientPool resourceToSave = createResource(null, "Test PatientPool", 60, true, true); // ID is null for new resource
        PatientPool savedResource = createResource(1L, "Test PatientPool", 60, true, true); // ID is now 1L, as if assigned by DB

        when(resourceRepository.save(resourceToSave)).thenReturn(savedResource); // Mock repository save behavior

        // Act
        boolean result = resourceServiceImp.saveResource(resourceToSave);

        // Assert
        assertTrue(result, "saveResource should return true on successful save");
        verify(resourceRepository, times(1)).save(resourceToSave); // Verify repository save was called once
    }

    /**
     * Tests that saveResource() throws IllegalArgumentException when a null PatientPool is provided.
     */
    @Test
    void saveResource_NullResource_ShouldThrowException() {
        // Arrange
        PatientPool nullResource = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceServiceImp.saveResource(nullResource),
                "Expected IllegalArgumentException for null PatientPool");

        verify(resourceRepository, never()).save(any(PatientPool.class)); // Verify save was never called
    }

    /**
     * Tests that saveResource() throws IllegalArgumentException when PatientPool name is null.
     */
    @Test
    void saveResource_NullResourceName_ShouldThrowException() {
        // Arrange
        PatientPool invalidResource = createResource(
                null,
                null,
                60, true, true); // PatientPool with null name

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceServiceImp.saveResource(invalidResource),
                "Expected IllegalArgumentException for null PatientPool name");

        verify(resourceRepository, never()).save(any(PatientPool.class)); // Verify save was never called
    }

    /**
     * Tests that saveResource() throws IllegalArgumentException when PatientPool name is empty.
     */
    @Test
    void saveResource_EmptyResourceName_ShouldThrowException() {
        // Arrange
        PatientPool invalidResource = createResource(
                null,
                "",
                60, true, true); // PatientPool with empty name

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceServiceImp.saveResource(invalidResource),
                "Expected IllegalArgumentException for empty PatientPool name");

        verify(resourceRepository, never()).save(any(PatientPool.class)); // Verify save was never called
    }

    /**
     * Tests that saveResource() throws IllegalArgumentException when processTime is zero.
     */
    @Test
    void saveResource_ZeroProcessTime_ShouldThrowException() {
        // Arrange
        PatientPool invalidResource = createResource(
                null,
                "Test PatientPool",
                0, true, true); // PatientPool with zero processTime

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceServiceImp.saveResource(invalidResource),
                "Expected IllegalArgumentException for zero process time");

        verify(resourceRepository, never()).save(any(PatientPool.class)); // Verify save was never called
    }

    /**
     * Tests that saveResource() throws IllegalArgumentException when processTime is negative.
     */
    @Test
    void saveResource_NegativeProcessTime_ShouldThrowException() {
        // Arrange
        PatientPool invalidResource = createResource(
                null,
                "Test PatientPool",
                -10, true, true); // PatientPool with negative processTime

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceServiceImp.saveResource(invalidResource),
                "Expected IllegalArgumentException for negative process time");

        verify(resourceRepository, never()).save(any(PatientPool.class)); // Verify save was never called
    }

    /**
     * Edge Case Test: Tests saving a resource that already has an ID set.
     * Behavior: Should still attempt to save (potentially update if ID exists in DB, or insert) without error.
     */
    @Test
    void saveResource_ResourceWithExistingId_ShouldSaveWithoutError() {
        // Arrange
        PatientPool resourceToSave = createResource(10L, "ExistingIDResource", 60, true, true); // PatientPool with ID 10 already set
        PatientPool savedResource = createResource(10L, "ExistingIDResource", 60, true, true); // Mock saved resource

        when(resourceRepository.save(resourceToSave)).thenReturn(savedResource); // Mock repository save

        // Act
        boolean result = resourceServiceImp.saveResource(resourceToSave);

        // Assert
        assertTrue(result, "saveResource should return true when saving with existing ID");
        verify(resourceRepository, times(1)).save(resourceToSave); // Verify repository save was called
    }

    /**
     * Edge Case Test: Tests saving a resource where active and useable are false.
     * Behavior: Should save the resource correctly with these flags set to false.
     */
    @Test
    void saveResource_ActiveAndUseableFalse_ShouldSaveSuccessfully() {
        // Arrange
        PatientPool resourceToSave = createResource(null, "Inactive PatientPool", 45, false, false);
        PatientPool savedResource = createResource(11L, "Inactive PatientPool", 45, false, false);

        when(resourceRepository.save(resourceToSave)).thenReturn(savedResource);

        // Act
        boolean result = resourceServiceImp.saveResource(resourceToSave);

        // Assert
        assertTrue(result, "saveResource should return true when active and useable are false");
        assertFalse(savedResource.isActive(), "Saved resource should have active as false");
        assertFalse(savedResource.isUseable(), "Saved resource should have useable as false");
        verify(resourceRepository, times(1)).save(resourceToSave);
    }


    // ==================== findAllResources TESTS ====================
    /**
     * Tests that findAllResources() returns a list of resources when resources exist.
     */
    @Test
    void findAllResources_ShouldReturnListOfResources() {
        // Arrange
        List<PatientPool> mockResources = List.of(
                createResource(1L, "PatientPool One", 30, true, true),
                createResource(2L, "PatientPool Two", 45, false, true)
        );
        when(resourceRepository.findAll()).thenReturn(mockResources); // Mock findAll to return mockResources

        // Act
        List<PatientPool> result = resourceServiceImp.findAllResources();

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(2, result.size(), "The result list should contain 2 resources");
        assertEquals(mockResources.get(0).getId(), result.get(0).getId(), "First resource ID should match");
        assertEquals(mockResources.get(1).getId(), result.get(1).getId(), "Second resource ID should match");
        verify(resourceRepository, times(1)).findAll(); // Verify findAll was called once
    }

    /**
     * Tests that findAllResources() returns an empty list when no resources exist.
     */
    @Test
    void findAllResources_ShouldReturnEmptyListWhenNoResources() {
        // Arrange
        when(resourceRepository.findAll()).thenReturn(Collections.emptyList()); // Mock findAll to return empty list

        // Act
        List<PatientPool> result = resourceServiceImp.findAllResources();

        // Assert
        assertNotNull(result, "The result should not be null");
        assertTrue(result.isEmpty(), "The result list should be empty");
        verify(resourceRepository, times(1)).findAll(); // Verify findAll was called once
    }

    /**
     * Edge Case Test: Tests findResourceById() with a zero ID.
     * Behavior: Should return an empty Optional as no resource is expected with ID 0.
     */
    @Test
    void findResourceById_ZeroId_ShouldReturnEmptyOptional() {
        // Arrange
        Long resourceId = 0L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty()); // Mock repository to return empty for ID 0

        // Act
        Optional<PatientPool> resultOptional = resourceServiceImp.findResourceById(resourceId);

        // Assert
        assertTrue(resultOptional.isEmpty(), "Expected no resource to be found for ID 0");
        verify(resourceRepository, times(1)).findById(resourceId);
    }

    /**
     * Edge Case Test: Tests findResourceById() with a negative ID.
     * Behavior: Should return an empty Optional as no resource is expected with negative ID.
     */
    @Test
    void findResourceById_NegativeId_ShouldReturnEmptyOptional() {
        // Arrange
        Long resourceId = -1L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty()); // Mock repository to return empty for ID -1

        // Act
        Optional<PatientPool> resultOptional = resourceServiceImp.findResourceById(resourceId);

        // Assert
        assertTrue(resultOptional.isEmpty(), "Expected no resource to be found for negative ID");
        verify(resourceRepository, times(1)).findById(resourceId);
    }


    // ==================== findResourceById TESTS ====================
    /**
     * Tests that findResourceById() returns the correct resource wrapped in an Optional
     * when the resource exists.
     */
    @Test
    void findResourceById_ResourceExists_ShouldReturnResourceOptional() {
        // Arrange
        Long resourceId = 123L;
        PatientPool mockResource = createResource(resourceId, "Existing PatientPool", 30, true, true);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(mockResource)); // Mock findById to return mockResource

        // Act
        Optional<PatientPool> resultOptional = resourceServiceImp.findResourceById(resourceId);

        // Assert
        assertTrue(resultOptional.isPresent(), "Expected a resource to be found");
        assertEquals(resourceId, resultOptional.get().getId(), "The returned resource ID should match");
        assertEquals(mockResource.getName(), resultOptional.get().getName(), "Returned resource name should match");
        verify(resourceRepository, times(1)).findById(resourceId); // Verify findById was called once with resourceId
    }

    /**
     * Tests that findResourceById() returns an empty Optional when the resource is not found.
     */
    @Test
    void findResourceById_ResourceNotFound_ShouldReturnEmptyOptional() {
        // Arrange
        Long resourceId = 456L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty()); // Mock findById to return empty Optional

        // Act
        Optional<PatientPool> resultOptional = resourceServiceImp.findResourceById(resourceId);

        // Assert
        assertTrue(resultOptional.isEmpty(), "Expected no resource to be found");
        verify(resourceRepository, times(1)).findById(resourceId); // Verify findById was called once with resourceId
    }

    // ==================== deleteResourceById TESTS ====================
    /**
     * Tests that deleteResourceById() succeeds when the resource exists.
     */
    @Test
    void deleteResourceById_ResourceExists_ShouldSucceed() {
        // Arrange
        Long resourceId = 789L;
        when(resourceRepository.existsById(resourceId)).thenReturn(true); // Mock existsById to return true (resource exists)
        doNothing().when(resourceRepository).deleteById(resourceId); // Mock deleteById to do nothing (void method)

        // Act & Assert
        assertDoesNotThrow(() -> resourceServiceImp.deleteResourceById(resourceId), // Execute deleteResourceById and assert no exception
                "Deleting an existing resource should not throw an exception");

        verify(resourceRepository, times(1)).existsById(resourceId); // Verify existsById was called once
        verify(resourceRepository, times(1)).deleteById(resourceId); // Verify deleteById was called once
    }

    /**
     * Tests that deleteResourceById() throws RuntimeException when the resource does not exist.
     */
    @Test
    void deleteResourceById_ResourceNotFound_ShouldThrowRuntimeException() {
        // Arrange
        Long nonExistentResourceId = 101L;
        when(resourceRepository.existsById(nonExistentResourceId)).thenReturn(false); // Mock existsById to return false (resource not found)

        // Act & Assert
        assertThrows(RuntimeException.class, () -> resourceServiceImp.deleteResourceById(nonExistentResourceId), // Execute deleteResourceById and assert exception
                "Expected RuntimeException when deleting a non-existent resource");

        verify(resourceRepository, times(1)).existsById(nonExistentResourceId); // Verify existsById was called once
        verify(resourceRepository, never()).deleteById(anyLong()); // Verify deleteById was never called
    }

}
