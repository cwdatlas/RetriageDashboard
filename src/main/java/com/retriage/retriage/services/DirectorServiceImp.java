package com.retriage.retriage.services;

import java.util.List;
import java.util.Optional;

import com.retriage.retriage.models.Director;
import com.retriage.retriage.repositories.DirectorRepository;
import org.springframework.stereotype.Service;

public class DirectorServiceImp implements DirectorService {
    private final DirectorRepository directorRepository;

    public DirectorServiceImp(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    /**
     * @param director
     * @return
     */
    @Override
    public Director saveDirector(Director director) {
        return directorRepository.save(director);
    }

    /**
     * @return
     */
    @Override
    public List<Director> findAllDirectors() {
        return directorRepository.findAll();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Director> findDirectorById(Long id) {
        return directorRepository.findById(id);
    }

    /**
     * @param id
     */
    @Override
    public void deleteDirectorById(Long id) {
        directorRepository.deleteById(id);
    }
}
