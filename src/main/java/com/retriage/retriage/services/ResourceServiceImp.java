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

    @Override
    public boolean saveResource(Resource resource) {
        return false;
    }

    @Override
    public List<Resource> findAllResources() {
        return List.of();
    }

    @Override
    public Optional<Resource> findResourceById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteResourceById(Long id) {

    }
}
