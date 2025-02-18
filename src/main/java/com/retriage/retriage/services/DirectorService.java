package com.retriage.retriage.services;

import com.retriage.retriage.models.Director;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface DirectorService {
    Director saveDirector(Director director);

    List<Director> findAllDirectors();

    Optional<Director> findDirectorById(Long id);

    void deleteDirectorById(Long id);
}
