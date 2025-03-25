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
        logger.info("Entering PatientPoolServiceImp constructor with poolRepository: {}", poolRepository);
        this.poolRepository = poolRepository;
        logger.info("Exiting PatientPoolServiceImp constructor");
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
        logger.info("Entering savePool with pool: {}", pool);
        logger.debug("savePool - Validating pool: {}", pool);
        try {
            validatePool(pool); // Call validatePool here, it will throw exception if invalid
            logger.debug("savePool - PatientPool validation passed."); // Log only if validation passes

            PatientPool savedPool = poolRepository.save(pool);
            boolean isSaved = savedPool != null;
            if (isSaved) {
                logger.info("savePool - PatientPool saved successfully with ID: {}", savedPool.getId());
                logger.debug("savePool - Saved PatientPool details: {}", savedPool);
            } else {
                logger.error("savePool - Failed to save pool: {}", pool);
            }
            logger.info("Exiting savePool, returning: {}", isSaved);
            return isSaved;
        } catch (IllegalArgumentException e) {
            logger.warn("savePool - PatientPool validation failed: {}", e.getMessage());
            logger.info("Exiting savePool, returning: false");
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
        logger.info("Entering findAllPool");
        logger.debug("findAllPools - Calling poolRepository.findAll()");
        List<PatientPool> pools = poolRepository.findAll();
        logger.info("findAllPools - Retrieved {} pools.", pools.size());
        logger.debug("findAllPools - Retrieved pool list: {}", pools);
        logger.info("Exiting findAllPool, returning list of size: {}", pools.size());
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
        logger.info("Entering findPoolById with id: {}", id);
        logger.debug("findPoolById - Calling poolRepository.findById({})", id);
        Optional<PatientPool> poolOptional = poolRepository.findById(id);
        if (poolOptional.isPresent()) {
            logger.info("findPoolById - PatientPool found with ID: {}", id);
            logger.debug("findPoolById - Found PatientPool details: {}", poolOptional.get());
        } else {
            logger.warn("findPoolById - No pool found with ID: {}", id);
        }
        logger.info("Exiting findPoolById, returning Optional with value present: {}", poolOptional.isPresent());
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
        logger.info("Entering deletePoolById with id: {}", id);
        logger.debug("deletePoolById - Checking if pool with ID {} exists", id);
        if (poolRepository.existsById(id)) {
            logger.debug("deletePoolById - PatientPool with ID {} exists. Proceeding with deletion.", id);
            poolRepository.deleteById(id);
            logger.info("deletePoolById - PatientPool deleted successfully with ID: {}", id);
        } else {
            logger.warn("deletePoolById - PatientPool with id {} does not exist.", id);
            logger.debug("deletePoolById - Attempted to delete non-existent pool with id: {}", id);
        }
        logger.info("Exiting deletePoolById");
    }

    /**
     * validatePool
     * Validates a PatientPool object before saving.
     *
     * @param pool The PatientPool object to validate.
     * @throws IllegalArgumentException if the pool is invalid.
     */
    private void validatePool(PatientPool pool) {
        logger.debug("Entering validatePool with pool: {}", pool);
        if (pool == null) {
            logger.warn("validatePool - PatientPool object is null.");
            throw new IllegalArgumentException("PatientPool object cannot be null.");
        }
        if (pool.getName() == null || pool.getName().trim().isEmpty()) {
            logger.warn("validatePool - PatientPool name is null or empty.");
            throw new IllegalArgumentException("PatientPool name cannot be null or empty.");
        }
        if (pool.getProcessTime() <= 0) {
            logger.warn("validatePool - Process time is not a positive number: {}", pool.getProcessTime());
            throw new IllegalArgumentException("Process time must be a positive number.");
        }
        logger.debug("Exiting validatePool - PatientPool validation passed.");
    }
}