package com.retriage.retriage.services;

import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.repositories.PatientPoolTmpRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PatientPoolTmpServiceImp implements PatientPoolTmpService {

    private static final Logger logger = LoggerFactory.getLogger(PatientPoolServiceImp.class);
    private final PatientPoolTmpRepo resourceTemplateRepository;

    /**
     * PatientPool Service constructor
     *
     * @param resourceTemplateRepository Repository declared in PatientPoolServiceImp
     */
    public PatientPoolTmpServiceImp(PatientPoolTmpRepo resourceTemplateRepository) {
        this.resourceTemplateRepository = resourceTemplateRepository;
    }

    /**
     * Saves/updates any resource, after first checking if it's not null
     *
     * @param resource the resource you're trying to save
     * @return True if the resource is not null
     */
    @Override
    public boolean saveResourceTmp(PatientPool resource) {
        logger.info("** Starting to save/update resource **");
        logger.debug("saveResourceTmp: PatientPool details - {}", resource);
        validateResourceTmp(resource); // Call validateResource here, it will throw exception if invalid
        logger.debug("saveResourceTmp: PatientPool validation passed."); // Log only if validation passes

        PatientPool savedResource = resourceTemplateRepository.save(resource);
        boolean isSaved = savedResource != null;
        if (isSaved) {
            logger.info("saveResourceTmp: PatientPool saved successfully with ID: {}", savedResource.getId());
            logger.debug("saveResourceTmp: Saved PatientPool details - {}", savedResource);
        } else {
            logger.error("saveResource: Failed to save resource.");
        }
        return isSaved;
    }

    /**
     * Find and pull every resource
     *
     * @return Every resource
     */
    @Override
    public List<PatientPool> findAllResourcesTmp() {
        logger.info("** Starting to retrieve all resources **");
        logger.debug("findAllResourcesTmp: About to call resourceRepository.findAll()");
        List<PatientPool> resources = resourceTemplateRepository.findAll();
        logger.debug("findAllResourcesTmp: Retrieved {} resources", resources.size());
        logger.info("findAllResourcesTmp: Successfully retrieved {} resources.", resources.size());
        return resources;
    }

    /**
     * Find a specific resource with a given ID
     *
     * @param id The ID of the resource to look for
     * @return The resource you're looking for
     */
    @Override
    public Optional<PatientPool> findResourceTmpById(Long id) {
        logger.info("** Starting to find resource by ID: {} **", id);
        logger.debug("findResourceByIdTmp: About to call resourceRepository.findById({})", id);
        Optional<PatientPool> resourceOptional = resourceTemplateRepository.findById(id);
        if (resourceOptional.isPresent()) {
            logger.debug("findResourceByIdTmp: PatientPool found with ID: {}", id);
            logger.info("findResourceByIdTmp: PatientPool found with ID: {}", id);
        } else {
            logger.warn("findResourceByIdTmp: No resource found with ID: {}", id);
        }
        return resourceOptional;
    }

    /**
     * Deletes a specified resource
     *
     * @param id The ID of the resource to be deleted
     */
    @Override
    public void deleteResourceTmpById(Long id) {
        logger.info("** Starting to delete resource with ID: {} **", id);
        logger.debug("deleteResourceByIdTmp: Checking if resource with ID {} exists", id);
        if (resourceTemplateRepository.existsById(id)) {
            logger.debug("deleteResourceByIdTmp: PatientPool with ID {} exists. Proceeding with deletion.", id);
            resourceTemplateRepository.deleteById(id);
            logger.info("deleteResourceByIdTmp: PatientPool deleted successfully with ID: {}", id);
        } else {
            String errorMessage = "PatientPool with id " + id + " does not exist.";
            logger.warn("deleteResourceByIdTmp: {}", errorMessage);
            logger.debug("deleteResourceByIdTmp: Throwing RuntimeException - {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Validates a PatientPool object before saving.
     *
     * @param resource The PatientPool object to validate.
     * @throws IllegalArgumentException if the resource is invalid.
     */
    private void validateResourceTmp(PatientPool resource) {
        logger.debug("** Starting resource validation **");
        if (resource == null) {
            logger.warn("validateResourceTmp: PatientPool object is null.");
            throw new IllegalArgumentException("PatientPool object cannot be null.");
        }
        if (resource.getName() == null || resource.getName().trim().isEmpty()) {
            logger.warn("validateResourceTmp: PatientPool name is null or empty.");
            throw new IllegalArgumentException("PatientPool name cannot be null or empty.");
        }
        if (resource.getProcessTime() <= 0) {
            logger.warn("validateResourceTmp: Process time is not a positive number: {}", resource.getProcessTime());
            throw new IllegalArgumentException("Process time must be a positive number.");
        }
        logger.debug("validateResourceTmp: PatientPool validation passed successfully.");
    }
}
