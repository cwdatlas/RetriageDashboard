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
        this.poolTemplateRepository = poolTemplateRepository;
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
     * findAllPoolTmp
     * Find and pull every pool
     *
     * @return Every pool
     */
    @Override
    public List<PatientPoolTmp> findAllPoolTmp() {
        List<PatientPoolTmp> pools = poolTemplateRepository.findAll();
        logger.info("findAllPoolsTmp - Retrieved {} pool templates.", pools.size());
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
        Optional<PatientPoolTmp> poolOptional = poolTemplateRepository.findById(id);
        if (poolOptional.isPresent()) {
            logger.info("findPoolByIdTmp - PatientPoolTmp found with ID: {}", id);
        } else {
            logger.warn("findPoolByIdTmp - No pool template found with ID: {}", id);
        }
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
        if (poolTemplateRepository.existsById(id)) {
            poolTemplateRepository.deleteById(id);
            logger.info("deletePoolByIdTmp - PatientPoolTmp deleted successfully with ID: {}", id);
        } else {
            logger.warn("deletePoolByIdTmp - PatientPoolTmp with id {} does not exist.", id);
        }
    }

    /**
     * validatePoolTmp
     * Validates a PatientPoolTmp object before saving.
     *
     * @param pool The PatientPoolTmp object to validate.
     * @throws IllegalArgumentException if the pool is invalid.
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
    }
}