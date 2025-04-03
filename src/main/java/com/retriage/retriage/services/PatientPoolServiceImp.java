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
     * PatientPoolServiceImp
     * PatientPool Service constructor
     *
     * @param poolRepository Repository declared in PatientPoolServiceImp
     */
    public PatientPoolServiceImp(PatientPoolRepo poolRepository) {
        this.poolRepository = poolRepository;
    }

    /**
     * savePool
     * Saves/updates any pool, after first checking if it's not null
     *
     * @param pool the pool you're trying to save
     * @return True if the pool is not null
     */
    @Override
    public boolean savePool(PatientPool pool) {
        try {
            validatePool(pool); // Call validatePool here, it will throw exception if invalid
            PatientPool savedPool = poolRepository.save(pool);
            boolean isSaved = savedPool != null;
            if (isSaved) {
                logger.info("savePool - PatientPool saved successfully with ID: {}", savedPool.getId());
            } else {
                logger.error("savePool - Failed to save pool");
            }
            return isSaved;
        } catch (IllegalArgumentException e) {
            logger.warn("savePool - PatientPool validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * findAllPool
     * Find and pull every pool
     *
     * @return Every pool
     */
    @Override
    public List<PatientPool> findAllPool() {
        List<PatientPool> pools = poolRepository.findAll();
        logger.info("findAllPools - Retrieved {} pools.", pools.size());
        return pools;
    }

    /**
     * findPoolById
     * Find a specific pool with a given ID
     *
     * @param id The ID of the pool to look for
     * @return The pool you're looking for
     */
    @Override
    public Optional<PatientPool> findPoolById(Long id) {
        Optional<PatientPool> poolOptional = poolRepository.findById(id);
        if (poolOptional.isPresent()) {
            logger.info("findPoolById - PatientPool found with ID: {}", id);
        } else {
            logger.warn("findPoolById - No pool found with ID: {}", id);
        }
        return poolOptional;
    }

    /**
     * deletePoolById
     * Deletes a specified pool
     *
     * @param id The ID of the pool to be deleted
     */
    @Override
    public void deletePoolById(Long id) {
        if (poolRepository.existsById(id)) {
            poolRepository.deleteById(id);
            logger.info("deletePoolById - PatientPool deleted successfully with ID: {}", id);
        } else {
            logger.warn("deletePoolById - PatientPool with id {} does not exist.", id);
        }
    }

    /**
     * validatePool
     * Validates a PatientPool object before saving.
     *
     * @param pool The PatientPool object to validate.
     * @throws IllegalArgumentException if the pool is invalid.
     */
    private void validatePool(PatientPool pool) {
        if (pool == null) {
            throw new IllegalArgumentException("PatientPool object cannot be null.");
        }
        if (pool.getName() == null || pool.getName().trim().isEmpty()) {
            logger.warn("validatePool - PatientPool name is null or empty.");
            throw new IllegalArgumentException("PatientPool name cannot be null or empty.");
        }
        if (pool.getProcessTime() <= 0) {
            logger.warn("validatePool - Process time is not a positive number");
            throw new IllegalArgumentException("Process time must be a positive number.");
        }
    }
}