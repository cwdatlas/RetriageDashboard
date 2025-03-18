package com.retriage.retriage.services;

import com.retriage.retriage.models.PatientPool;

import java.util.List;
import java.util.Optional;

/**
 *
 */
//TODO add all CRUD functions. Create Read Update and Delete. Some are already here, but more needs to be done
public interface PatientPoolTmpService {
    boolean savePoolTmp(PatientPool resource);

    List<PatientPool> findAllPoolTmp();

    Optional<PatientPool> findPoolTmpById(Long id);

    void deletePoolTmpById(Long id);
}
