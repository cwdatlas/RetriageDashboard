package com.retriage.retriage.services;

import com.retriage.retriage.models.Resource;
import com.retriage.retriage.repositories.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ResourceServiceImp implements ResourceService {

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
        // Save resource and return true if successful
        return resourceRepository.save(resource) != null;
    }

    /**
     * Find and pull every resource
     *
     * @return Every resource
     */
    @Override
    public List<Resource> findAllResources() {
        return resourceRepository.findAll();
    }

    /**
     * Find a specific resource with a given ID
     *
     * @param id The ID of the resource to look for
     * @return The resource you're looking for
     */
    @Override
    public Optional<Resource> findResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    /**
     * Deletes a specified resource
     *
     * @param id The ID of the resource to be deleted
     */
    @Override
    public void deleteResourceById(Long id) {
        if (resourceRepository.existsById(id)) {
            resourceRepository.deleteById(id);
        } else {
            throw new RuntimeException("Resource with id " + id + " does not exist.");
        }

    }
}
