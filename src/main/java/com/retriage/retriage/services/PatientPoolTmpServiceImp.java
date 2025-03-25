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
     * PatientPoolTmpServiceImp
     *
     * @param poolTemplateRepository Repository declared in PatientPoolTmpServiceImp
     */
    public PatientPoolTmpServiceImp(PatientPoolTmpRepo poolTemplateRepository) {
        logger.info("Entering PatientPoolTmpServiceImp constructor with poolTemplateRepository: {}", poolTemplateRepository);
        this.poolTemplateRepository = poolTemplateRepository;
        logger.info("Exiting PatientPoolTmpServiceImp constructor");
    }

    /**
     * savePoolTmp
     * Saves/updates any pool, after first checking if it's not null
     *
     * @param pool the pool you're trying to save
     * @return True if the pool is not null
     */
    @Override
    public boolean savePoolTmp(PatientPoolTmp pool) {
        logger.info("Entering savePoolTmp with pool: {}", pool);
        logger.debug("savePoolTmp - Validating pool template: {}", pool);
        try {
            validatePoolTmp(pool); // Call validatePool here, it will throw exception if invalid
            logger.debug("savePoolTmp - PatientPoolTmp validation passed."); // Log only if validation passes

            PatientPoolTmp savedPool = poolTemplateRepository.save(pool);
            boolean isSaved = savedPool != null;
            if (isSaved) {
                logger.info("savePoolTmp - PatientPoolTmp saved successfully with ID: {}", savedPool.getId());
                logger.debug("savePoolTmp - Saved PatientPoolTmp details: {}", savedPool);
            } else {
                logger.error("savePoolTmp - Failed to save pool template: {}", pool);
            }
            logger.info("Exiting savePoolTmp, returning: {}", isSaved);
            return isSaved;
        } catch (IllegalArgumentException e) {
            logger.warn("savePoolTmp - PatientPoolTmp validation failed: {}", e.getMessage());
            logger.info("Exiting savePoolTmp, returning: false");
            return false;
        }
    }

    /**
     * findAllPoolTmp
     * Find and pull every pool
     *
     * @return Every pool
     */
    @Override
    public List<PatientPoolTmp> findAllPoolTmp() {
        logger.info("Entering findAllPoolTmp");
        logger.debug("findAllPoolsTmp - Calling poolTemplateRepository.findAll()");
        List<PatientPoolTmp> pools = poolTemplateRepository.findAll();
        logger.info("findAllPoolsTmp - Retrieved {} pool templates.", pools.size());
        logger.debug("findAllPoolsTmp - Retrieved pool template list: {}", pools);
        logger.info("Exiting findAllPoolTmp, returning list of size: {}", pools.size());
        return pools;
    }

    /**
     * findPoolTmpById
     * Find a specific pool with a given ID
     *
     * @param id The ID of the pool to look for
     * @return The pool you're looking for
     */
    @Override
    public Optional<PatientPoolTmp> findPoolTmpById(Long id) {
        logger.info("Entering findPoolTmpById with id: {}", id);
        logger.debug("findPoolByIdTmp - Calling poolTemplateRepository.findById({})", id);
        Optional<PatientPoolTmp> poolOptional = poolTemplateRepository.findById(id);
        if (poolOptional.isPresent()) {
            logger.info("findPoolByIdTmp - PatientPoolTmp found with ID: {}", id);
            logger.debug("findPoolByIdTmp - Found PatientPoolTmp details: {}", poolOptional.get());
        } else {
            logger.warn("findPoolByIdTmp - No pool template found with ID: {}", id);
        }
        logger.info("Exiting findPoolTmpById, returning Optional with value present: {}", poolOptional.isPresent());
        return poolOptional;
    }

    /**
     * deletePoolTmpById
     * Deletes a specified pool
     *
     * @param id The ID of the pool to be deleted
     */
    @Override
    public void deletePoolTmpById(Long id) {
        logger.info("Entering deletePoolTmpById with id: {}", id);
        logger.debug("deletePoolByIdTmp - Checking if pool template with ID {} exists", id);
        if (poolTemplateRepository.existsById(id)) {
            logger.debug("deletePoolByIdTmp - PatientPoolTmp with ID {} exists. Proceeding with deletion.", id);
            poolTemplateRepository.deleteById(id);
            logger.info("deletePoolByIdTmp - PatientPoolTmp deleted successfully with ID: {}", id);
        } else {
            logger.warn("deletePoolByIdTmp - PatientPoolTmp with id {} does not exist.", id);
            logger.debug("deletePoolByIdTmp - Attempted to delete non-existent pool template with id: {}", id);
        }
        logger.info("Exiting deletePoolTmpById");
    }

    /**
     * validatePoolTmp
     * Validates a PatientPoolTmp object before saving.
     *
     * @param pool The PatientPoolTmp object to validate.
     * @throws IllegalArgumentException if the pool is invalid.
     */
    private void validatePoolTmp(PatientPoolTmp pool) {
        logger.debug("Entering validatePoolTmp with pool: {}", pool);
        if (pool == null) {
            logger.warn("validatePoolTmp - PatientPoolTmp object is null.");
            throw new IllegalArgumentException("PatientPoolTmp object cannot be null.");
        }
        if (pool.getName() == null || pool.getName().trim().isEmpty()) {
            logger.warn("validatePoolTmp - PatientPoolTmp name is null or empty.");
            throw new IllegalArgumentException("PatientPoolTmp name cannot be null or empty.");
        }
        logger.debug("Exiting validatePoolTmp - PatientPoolTmp validation passed.");
    }
}