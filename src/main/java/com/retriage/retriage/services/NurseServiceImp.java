package com.retriage.retriage.services;

import java.util.List;
import java.util.Optional;

import com.retriage.retriage.repositories.NurseRepository;
import org.springframework.stereotype.Service;
import com.retriage.retriage.models.Nurse;


@Service
public class NurseServiceImp implements NurseService {

    private final NurseRepository nurseRepository;


    public NurseServiceImp(NurseRepository nurseRepository) {
        this.nurseRepository = nurseRepository;
    }

    // Create or update a Nurse
    public Nurse saveNurse(Nurse nurse) {
        return this.nurseRepository.save(nurse);
    }

    /**
     * @return
     */
    @Override
    public List<Nurse> findAllNurses() {
        return List.of();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Nurse> findNurseById(Long id) {
        return Optional.empty();
    }

    /**
     * @param id
     */
    @Override
    public void deleteNurse(Long id) {

    }

    // Retrieve all Nurses
    public List<Nurse> getAllNurses() {
        return this.nurseRepository.findAll();
    }
    // Retrieve a single Nurse by ID
    public Optional<Nurse> getNurseById(Long id) {
        return this.nurseRepository.findById(id);
    }
    // Optional: delete, update status, etc
    public void deleteNurseById(Long id) {
        this.nurseRepository.deleteById(id);
    }
}
