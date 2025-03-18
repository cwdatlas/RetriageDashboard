package com.retriage.retriage.services;

import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.repositories.PatientPoolRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PatientPoolServiceImp implements PatientPoolService {

    private static final Logger logger = LoggerFactory.getLogger(PatientPoolServiceImp.class);
    private final PatientPoolRepo poolRepository;

    /**
     * PatientPool Service constructor
     *
     * @param poolRepository Repository declared in PatientPoolServiceImp
     */
    public PatientPoolServiceImp(PatientPoolRepo poolRepository) {
        this.poolRepository = poolRepository;
    }

    /**
     * Saves/updates any pool, after first checking if it's not null
     *
     * @param pool the pool you're trying to save
     * @return True if the pool is not null
     */
    @Override
    public boolean savePool(PatientPool pool) {
        logger.info("** Starting to save/update pool **");
        logger.debug("savePool: PatientPool details - {}", pool);
        validatePool(pool); // Call validatePool here, it will throw exception if invalid
        logger.debug("savePool: PatientPool validation passed."); // Log only if validation passes

        PatientPool savedPool = poolRepository.save(pool);
        boolean isSaved = savedPool != null;
        if (isSaved) {
            logger.info("savePool: PatientPool saved successfully with ID: {}", savedPool.getId());
            logger.debug("savePool: Saved PatientPool details - {}", savedPool);
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
    public List<PatientPool> findAllPool() {
        logger.info("** Starting to retrieve all pools **");
        logger.debug("findAllPools: About to call poolRepository.findAll()");
        List<PatientPool> pools = poolRepository.findAll();
        logger.debug("findAllPools: Retrieved {} pools", pools.size());
        logger.info("findAllPools: Successfully retrieved {} pools.", pools.size());
        return pools;
    }

    /**
     * Find a specific pool with a given ID
     *
     * @param id The ID of the pool to look for
     * @return The pool you're looking for
     */
    @Override
    public Optional<PatientPool> findPoolById(Long id) {
        logger.info("** Starting to find pool by ID: {} **", id);
        logger.debug("findPoolById: About to call poolRepository.findById({})", id);
        Optional<PatientPool> poolOptional = poolRepository.findById(id);
        if (poolOptional.isPresent()) {
            logger.debug("findPoolById: PatientPool found with ID: {}", id);
            logger.info("findPoolById: PatientPool found with ID: {}", id);
        } else {
            logger.warn("findPoolById: No pool found with ID: {}", id);
        }
        return poolOptional;
    }

    /**
     * Deletes a specified pool
     *
     * @param id The ID of the pool to be deleted
     */
    @Override
    public void deletePoolById(Long id) {
        logger.info("** Starting to delete pool with ID: {} **", id);
        logger.debug("deletePoolById: Checking if pool with ID {} exists", id);
        if (poolRepository.existsById(id)) {
            logger.debug("deletePoolById: PatientPool with ID {} exists. Proceeding with deletion.", id);
            poolRepository.deleteById(id);
            logger.info("deletePoolById: PatientPool deleted successfully with ID: {}", id);
        } else {
            String errorMessage = "PatientPool with id " + id + " does not exist.";
            logger.warn("deletePoolById: {}", errorMessage);
            logger.debug("deletePoolById: Throwing RuntimeException - {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Validates a PatientPool object before saving.
     *
     * @param pool The PatientPool object to validate.
     * @throws IllegalArgumentException if the pool is invalid.
     */
    private void validatePool(PatientPool pool) {
        logger.debug("** Starting pool validation **");
        if (pool == null) {
            logger.warn("validatePool: PatientPool object is null.");
            throw new IllegalArgumentException("PatientPool object cannot be null.");
        }
        if (pool.getName() == null || pool.getName().trim().isEmpty()) {
            logger.warn("validatePool: PatientPool name is null or empty.");
            throw new IllegalArgumentException("PatientPool name cannot be null or empty.");
        }
        if (pool.getProcessTime() <= 0) {
            logger.warn("validatePool: Process time is not a positive number: {}", pool.getProcessTime());
            throw new IllegalArgumentException("Process time must be a positive number.");
        }
        logger.debug("validatePool: PatientPool validation passed successfully.");
    }
}
