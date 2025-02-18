package com.retriage.retriage.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.retriage.retriage.models.Director;
import com.retriage.retriage.services.DirectorService;
@RestController
@CrossOrigin
@RequestMapping("/d")
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {this.directorService = directorService;}

    /**
     *
     * @param director
     * @return
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Director> createDirector(@RequestBody Director director) {
        Director saved = directorService.saveDirector(director);
        return ResponseEntity.
                created(URI.create("/d/" + saved.getId()))
                .body(saved);
    }
    // 2) Get all Patients
    // GET /patients
    @GetMapping(produces = "application/json")
    public List<Director> getAllDirectors() {
        return directorService.findAllDirectors();
    }

    // 3) Get one Patient by ID
    // GET /patients/{id}
    @GetMapping(value = "/d/{id}", produces = "application/json")
    public ResponseEntity<Director> getPatientById(@PathVariable Long id) {
        Optional<Director> optionalDirector = directorService.findDirectorById(id);
        return optionalDirector
                .map(director -> ResponseEntity.ok(director))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // OPTIONAL: Update or partial updates (PUT/PATCH) and Delete
    // For completeness, here's a simple delete example

    // 4) Delete a Patient
    // DELETE /patients/{id}
    @DeleteMapping("/d/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        directorService.deleteDirectorById(id);
        return ResponseEntity.noContent().build();
    }
}
