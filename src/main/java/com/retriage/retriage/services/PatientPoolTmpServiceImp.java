package com.retriage.retriage.services;

import com.retriage.retriage.models.PatientPoolTmp;
import com.retriage.retriage.repositories.PatientPoolTmpRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * Implementation of the {@link PatientPoolTmpService} interface.
 * Provides the concrete business logic for managing {@link PatientPoolTmp} entities,
 * interacting with the database via {@link PatientPoolTmpRepo}.
 */
@Service
public class PatientPoolTmpServiceImp implements PatientPoolTmpService {

    /**
     * Logger for this service implementation.
     */
    private static final Logger logger = LoggerFactory.getLogger(PatientPoolTmpServiceImp.class);
    /**
     * Repository for accessing and managing PatientPoolTmp entities in the database.
     */
    private final PatientPoolTmpRepo poolTemplateRepository;

    /**
     * Constructs an instance of {@code PatientPoolTmpServiceImp}.
     *
     * @param poolTemplateRepository The {@link PatientPoolTmpRepo} used for database operations on patient pool templates.
     */
    public PatientPoolTmpServiceImp(PatientPoolTmpRepo poolTemplateRepository) {
        this.poolTemplateRepository = poolTemplateRepository;
    }

    /**
     * Saves a patient pool template (Create/Update) in the database.
     * Performs validation before saving.
     *
     * @param pool The patient pool template to save.
     * @return true if the patient pool template is saved successfully, false otherwise (e.g., validation failure).
     */
    @Override
    public boolean savePoolTmp(PatientPoolTmp pool) {
        try {
            validatePoolTmp(pool); // Call validatePool here, it will throw exception if invalid
            logger.info("savePoolTmp - PatientPoolTmp validation passed."); // Log only if validation passes

            PatientPoolTmp savedPool = poolTemplateRepository.save(pool);
            if (savedPool != null) {
                logger.info("savePoolTmp - Pool template saved successfully with ID: {}", savedPool.getId());
                return true;
            } else {
                logger.error("savePoolTmp - Pool template save failed.");
                return false;
            }
        } catch (IllegalArgumentException e) {
            logger.warn("savePoolTmp - Pool template save failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all existing patient pool templates from the database.
     *
     * @return List of all patient pool templates. Returns an empty list if no templates exist.
     */
    @Override
    public List<PatientPoolTmp> findAllPoolTmp() {
        List<PatientPoolTmp> pools = poolTemplateRepository.findAll();
        logger.info("findAllPoolsTmp - Retrieved {} pool templates.", pools.size());
        return pools;
    }

    /**
     * Finds a specific patient pool template by its unique ID.
     *
     * @param id The ID of the patient pool template to look for.
     * @return The patient pool template if found, otherwise {@code null}.
     */
    @Override
    public PatientPoolTmp findPoolTmpById(Long id) {
        Optional<PatientPoolTmp> poolOptional = poolTemplateRepository.findById(id);
        if (poolOptional.isPresent()) {
            logger.info("findPoolByIdTmp - PatientPoolTmp found with ID: {}", id);
            return poolOptional.get();
        } else {
            logger.warn("findPoolByIdTmp - No pool template found with ID: {}", id);
            return null;
        }

    }

    /**
     * Deletes a specified patient pool template by its ID.
     * Checks if the template exists before attempting deletion.
     *
     * @param id The ID of the patient pool template to be deleted.
     */
    @Override
    public void deletePoolTmpById(Long id) {
        if (poolTemplateRepository.existsById(id)) {
            poolTemplateRepository.deleteById(id);
            logger.info("deletePoolByIdTmp - PatientPoolTmp deleted successfully with ID: {}", id);
        } else {
            logger.warn("deletePoolByIdTmp - PatientPoolTmp with id {} does not exist.", id);
        }
    }

    /**
     * Validates a PatientPoolTmp object before saving or updating.
     * Checks for null object and null/empty name.
     *
     * @param pool The PatientPoolTmp object to validate.
     * @throws IllegalArgumentException if the pool is invalid (e.g., null object or null/empty name).
     */
    private void validatePoolTmp(PatientPoolTmp pool) {
        if (pool == null) {
            logger.warn("validatePoolTmp - PatientPoolTmp object is null.");
            throw new IllegalArgumentException("PatientPoolTmp object cannot be null.");
        }
        if (pool.getName() == null || pool.getName().trim().isEmpty()) {
            logger.warn("validatePoolTmp - PatientPoolTmp name is null or empty.");
            throw new IllegalArgumentException("PatientPoolTmp name cannot be null or empty.");
        }
        if (pool.getProcessTime() == null || pool.getProcessTime() < 0) {
            logger.warn("validatePoolTmp - Process time is null or negative.");
            throw new IllegalArgumentException("Process time must be a non-negative value.");
        }
        if (pool.getPoolNumber() < 1) {
            logger.warn("validatePoolTmp - Pool number is less than 1.");
            throw new IllegalArgumentException("Pool number must be at least 1.");
        }
    }
}