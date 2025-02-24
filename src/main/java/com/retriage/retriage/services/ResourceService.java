package com.retriage.retriage.services;

import com.retriage.retriage.models.Resource;

import java.util.List;
import java.util.Optional;

/**
 *
 */
//TODO add all CRUD functions. Create Read Update and Delete. Some are already here, but more needs to be done
public interface ResourceService {
    boolean saveResource(Resource resource);

    List<Resource> findAllResources();

    Optional<Resource> findResourceById(Long id);

    void deleteResourceById(Long id);
}
