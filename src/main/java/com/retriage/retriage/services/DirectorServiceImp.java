package com.retriage.retriage.services;

import java.util.List;
import java.util.Optional;

import com.retriage.retriage.models.Director;
import com.retriage.retriage.repositories.DirectorRepository;
import org.springframework.stereotype.Service;

@Service
public class DirectorServiceImp implements DirectorService {

    private final DirectorRepository directorRepository;

    /**
     * Director Service constructor
     * @param directorRepository Repository declared in DirectorServiceImp
     */
    public DirectorServiceImp(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    /**
     * Saves a director
     * @param director
     * @return
     */
    public Director saveDirector(Director director) {
        return directorRepository.save(director);
    }

    /**
     * Finds all currently saved Director accounts
     * @return
     */
    public List<Director> findAllDirectors() {
        return directorRepository.findAll();
    }

    /**
     * Finds a director via their ID
     * @param id The ID of the Director you're looking for
     * @return The Director object assigned to the passed in ID
     */
    public Optional<Director> findDirectorById(Long id) {
        return directorRepository.findById(id);
    }

    /**
     * Remove a Director from saved list.
     * @param id The ID of the director to be deleted
     */
    public void deleteDirectorById(Long id) {
        directorRepository.deleteById(id);
    }
}
