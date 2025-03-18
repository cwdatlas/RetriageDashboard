package com.retriage.retriage.services;

import com.retriage.retriage.models.PatientPool;

import java.util.List;
import java.util.Optional;

/**
 *
 */
//TODO add all CRUD functions. Create Read Update and Delete. Some are already here, but more needs to be done
public interface ResourceTemplateService {
    boolean saveResourceTmp(PatientPool resource);

    List<PatientPool> findAllResourcesTmp();

    Optional<PatientPool> findResourceTmpById(Long id);

    void deleteResourceTmpById(Long id);
}
