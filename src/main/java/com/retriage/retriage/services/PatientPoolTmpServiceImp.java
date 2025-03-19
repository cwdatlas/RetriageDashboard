package com.retriage.retriage.services;

import com.retriage.retriage.models.PatientPoolTmp;
import com.retriage.retriage.repositories.PatientPoolTmpRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PatientPoolTmpServiceImp implements PatientPoolTmpService {

    private static final Logger logger = LoggerFactory.getLogger(PatientPoolTmpServiceImp.class);
    private final PatientPoolTmpRepo poolTemplateRepository;

    /**
     * PatientPoolTmp Service constructor
     *
     * @param poolTemplateRepository Repository declared in PatientPoolTmpServiceImp
     */
    public PatientPoolTmpServiceImp(PatientPoolTmpRepo poolTemplateRepository) {
        this.poolTemplateRepository = poolTemplateRepository;
    }

    /**
     * Saves/updates any pool, after first checking if it's not null
     *
     * @param pool the pool you're trying to save
     * @return True if the pool is not null
     */
    @Override
    public boolean savePoolTmp(PatientPoolTmp pool) {
        logger.info("** Starting to save/update pool **");
        logger.debug("savePoolTmp: PatientPoolTmp details - {}", pool);
        validatePoolTmp(pool); // Call validatePool here, it will throw exception if invalid
        logger.debug("savePoolTmp: PatientPoolTmp validation passed."); // Log only if validation passes

        PatientPoolTmp savedPool = poolTemplateRepository.save(pool);
        boolean isSaved = savedPool != null;
        if (isSaved) {
            logger.info("savePoolTmp: PatientPoolTmp saved successfully with ID: {}", savedPool.getId());
            logger.debug("savePoolTmp: Saved PatientPoolTmp details - {}", savedPool);
        } else {
            logger.error("savePool: Failed to save pool.");
        }
        return isSaved;
    }

    /**
     * Find and pull every pool
     *
     * @return Every pool
     */
    @Override
    public List<PatientPoolTmp> findAllPoolTmp() {
        logger.info("** Starting to retrieve all pools **");
        logger.debug("findAllPoolsTmp: About to call poolRepository.findAll()");
        List<PatientPoolTmp> pools = poolTemplateRepository.findAll();
        logger.debug("findAllPoolsTmp: Retrieved {} pools", pools.size());
        logger.info("findAllPoolsTmp: Successfully retrieved {} pools.", pools.size());
        return pools;
    }

    /**
     * Find a specific pool with a given ID
     *
     * @param id The ID of the pool to look for
     * @return The pool you're looking for
     */
    @Override
    public Optional<PatientPoolTmp> findPoolTmpById(Long id) {
        logger.info("** Starting to find pool by ID: {} **", id);
        logger.debug("findPoolByIdTmp: About to call poolRepository.findById({})", id);
        Optional<PatientPoolTmp> poolOptional = poolTemplateRepository.findById(id);
        if (poolOptional.isPresent()) {
            logger.debug("findPoolByIdTmp: PatientPoolTmp found with ID: {}", id);
            logger.info("findPoolByIdTmp: PatientPoolTmp found with ID: {}", id);
        } else {
            logger.warn("findPoolByIdTmp: No pool found with ID: {}", id);
        }
        return poolOptional;
    }

    /**
     * Deletes a specified pool
     *
     * @param id The ID of the pool to be deleted
     */
    @Override
    public void deletePoolTmpById(Long id) {
        logger.info("** Starting to delete pool with ID: {} **", id);
        logger.debug("deletePoolByIdTmp: Checking if pool with ID {} exists", id);
        if (poolTemplateRepository.existsById(id)) {
            logger.debug("deletePoolByIdTmp: PatientPoolTmp with ID {} exists. Proceeding with deletion.", id);
            poolTemplateRepository.deleteById(id);
            logger.info("deletePoolByIdTmp: PatientPoolTmp deleted successfully with ID: {}", id);
        } else {
            String errorMessage = "PatientPoolTmp with id " + id + " does not exist.";
            logger.warn("deletePoolByIdTmp: {}", errorMessage);
            logger.debug("deletePoolByIdTmp: Throwing RuntimeException - {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Validates a PatientPoolTmp object before saving.
     *
     * @param pool The PatientPoolTmp object to validate.
     * @throws IllegalArgumentException if the pool is invalid.
     */
    private void validatePoolTmp(PatientPoolTmp pool) {
        logger.debug("** Starting pool validation **");
        if (pool == null) {
            logger.warn("validatePoolTmp: PatientPoolTmp object is null.");
            throw new IllegalArgumentException("PatientPoolTmp object cannot be null.");
        }
        if (pool.getName() == null || pool.getName().trim().isEmpty()) {
            logger.warn("validatePoolTmp: PatientPoolTmp name is null or empty.");
            throw new IllegalArgumentException("PatientPoolTmp name cannot be null or empty.");
        }
        if (pool.getProcessTime() <= 0) {
            logger.warn("validatePoolTmp: Process time is not a positive number: {}", pool.getProcessTime());
            throw new IllegalArgumentException("Process time must be a positive number.");
        }
        logger.debug("validatePoolTmp: PatientPoolTmp validation passed successfully.");
    }
}
