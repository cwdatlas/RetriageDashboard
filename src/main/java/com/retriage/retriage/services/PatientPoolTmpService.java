package com.retriage.retriage.services;

import com.retriage.retriage.models.PatientPoolTmp;

import java.util.List;

/**
 * Service interface defining the contract for managing {@link PatientPoolTmp} entities.
 * Implementations of this interface handle the business logic related to patient pool templates,
 * used for configuring pools when creating new events.
 */
public interface PatientPoolTmpService {

    /**
     * Saves a given {@link PatientPoolTmp} entity.
     * This method can be used for both creating a new template and updating an existing one.
     *
     * @param resource The {@link PatientPoolTmp} to save.
     * @return {@code true} if the patient pool template was saved successfully, {@code false} otherwise.
     */
    boolean savePoolTmp(PatientPoolTmp resource);

    /**
     * Retrieves all existing {@link PatientPoolTmp} entities.
     *
     * @return A {@link List} of all {@link PatientPoolTmp} entities. Returns an empty list if no templates exist.
     */
    List<PatientPoolTmp> findAllPoolTmp();

    /**
     * Finds a {@link PatientPoolTmp} entity by its unique identifier.
     *
     * @param id The unique ID of the patient pool template to find.
     * @return The {@link PatientPoolTmp} entity if found, otherwise {@code null}.
     */
    PatientPoolTmp findPoolTmpById(Long id);

    /**
     * Deletes a {@link PatientPoolTmp} entity by its unique identifier.
     *
     * @param id The unique ID of the patient pool template to delete.
     */
    void deletePoolTmpById(Long id);
}