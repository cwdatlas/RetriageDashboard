package com.retriage.retriage.services;

import com.retriage.retriage.models.Resource;
import com.retriage.retriage.repositories.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ResourceServiceImp implements ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImp.class);
    private final ResourceRepository resourceRepository;

    /**
     * Resource Service constructor
     *
     * @param resourceRepository Repository declared in ResourceServiceImp
     */
    public ResourceServiceImp(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    /**
     * Saves/updates any resource, after first checking if it's not null
     *
     * @param resource the resource you're trying to save
     * @return True if the resource is not null
     */
    @Override
    public boolean saveResource(Resource resource) {
        logger.info("** Starting to save/update resource **");
        logger.debug("saveResource: Resource details - {}", resource);
        validateResource(resource); // Call validateResource here, it will throw exception if invalid
        logger.debug("saveResource: Resource validation passed."); // Log only if validation passes

        Resource savedResource = resourceRepository.save(resource);
        boolean isSaved = savedResource != null;
        if (isSaved) {
            logger.info("saveResource: Resource saved successfully with ID: {}", savedResource.getId());
            logger.debug("saveResource: Saved Resource details - {}", savedResource);
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
    public List<Resource> findAllResources() {
        logger.info("** Starting to retrieve all resources **");
        logger.debug("findAllResources: About to call resourceRepository.findAll()");
        List<Resource> resources = resourceRepository.findAll();
        logger.debug("findAllResources: Retrieved {} resources", resources.size());
        logger.info("findAllResources: Successfully retrieved {} resources.", resources.size());
        return resources;
    }

    /**
     * Find a specific resource with a given ID
     *
     * @param id The ID of the resource to look for
     * @return The resource you're looking for
     */
    @Override
    public Optional<Resource> findResourceById(Long id) {
        logger.info("** Starting to find resource by ID: {} **", id);
        logger.debug("findResourceById: About to call resourceRepository.findById({})", id);
        Optional<Resource> resourceOptional = resourceRepository.findById(id);
        if (resourceOptional.isPresent()) {
            logger.debug("findResourceById: Resource found with ID: {}", id);
            logger.info("findResourceById: Resource found with ID: {}", id);
        } else {
            logger.warn("findResourceById: No resource found with ID: {}", id);
        }
        return resourceOptional;
    }

    /**
     * Deletes a specified resource
     *
     * @param id The ID of the resource to be deleted
     */
    @Override
    public void deleteResourceById(Long id) {
        logger.info("** Starting to delete resource with ID: {} **", id);
        logger.debug("deleteResourceById: Checking if resource with ID {} exists", id);
        if (resourceRepository.existsById(id)) {
            logger.debug("deleteResourceById: Resource with ID {} exists. Proceeding with deletion.", id);
            resourceRepository.deleteById(id);
            logger.info("deleteResourceById: Resource deleted successfully with ID: {}", id);
        } else {
            String errorMessage = "Resource with id " + id + " does not exist.";
            logger.warn("deleteResourceById: {}", errorMessage);
            logger.debug("deleteResourceById: Throwing RuntimeException - {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Validates a Resource object before saving.
     * @param resource The Resource object to validate.
     * @throws IllegalArgumentException if the resource is invalid.
     */
    private void validateResource(Resource resource) {
        logger.debug("** Starting resource validation **");
        if (resource == null) {
            logger.warn("validateResource: Resource object is null.");
            throw new IllegalArgumentException("Resource object cannot be null.");
        }
        if (resource.getName() == null || resource.getName().trim().isEmpty()) {
            logger.warn("validateResource: Resource name is null or empty.");
            throw new IllegalArgumentException("Resource name cannot be null or empty.");
        }
        if (resource.getProcessTime() <= 0) {
            logger.warn("validateResource: Process time is not a positive number: {}", resource.getProcessTime());
            throw new IllegalArgumentException("Process time must be a positive number.");
        }
        logger.debug("validateResource: Resource validation passed successfully.");
    }
}
