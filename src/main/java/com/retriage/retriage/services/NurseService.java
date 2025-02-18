package com.retriage.retriage.services;

import com.retriage.retriage.models.Nurse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface NurseService {

    Nurse saveNurse(Nurse nurse);

    List<Nurse> findAllNurses();

    Optional<Nurse> findNurseById(Long id);

    void deleteNurse(Long id);
}
