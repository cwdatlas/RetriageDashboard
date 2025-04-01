package com.retriage.retriage.controllers;

import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.forms.PatientPoolTmpForm;
import com.retriage.retriage.models.PatientPoolTmp;
import com.retriage.retriage.services.PatientPoolTmpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/pools/templates")
public class PatientPoolTmpController {
    private static final Logger log = LoggerFactory.getLogger(PatientPoolTmpController.class);
    /**
     *
     */
    private final PatientPoolTmpService poolService;

    /**
     * Constructor injection of the service
     */
    public PatientPoolTmpController(PatientPoolTmpService poolService) {
        this.poolService = poolService;
    }

    /**
     * 1) Create a new PatientPool
     * POST /templates
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createPool(@RequestBody PatientPoolTmpForm poolForm) {
        PatientPoolTmp newPool = new PatientPoolTmp();
        newPool.setName(poolForm.getName());
        newPool.setUseable(poolForm.isUseable());
        newPool.setProcessTime(poolForm.getProcessTime());
        newPool.setPoolType(poolForm.getPoolType());
        newPool.setQueueSize(poolForm.getQueueSize());

        boolean saved = poolService.savePoolTmp(newPool);
        if (saved) {
            log.debug("createPool: Saved new pool Template name '{}'", newPool.getName());
            return ResponseEntity.created(URI.create("/templates")).body(newPool); // Body is PatientPoolTmp
        } else {
            log.warn("createPool: Unable to save template name'{}'", newPool.getName());
            ErrorResponse errorResponse = new ErrorResponse(List.of("Unable to save pool template."), HttpStatus.INTERNAL_SERVER_ERROR.value(), "SAVE_FAILED");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // Body is ErrorResponse
        }
    }

    /**
     * 2) Get all Patients
     * GET /templates
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<PatientPoolTmp>> getAllPools() {
        List<PatientPoolTmp> pools = poolService.findAllPoolTmp();
        log.debug("Found {} pools", pools.size());
        log.debug("Pools '{}' found", pools);
        return new ResponseEntity<>(pools, HttpStatus.OK); // Body is List<PatientPoolTmp>
    }

    /**
     * 3) Get one Patient by ID
     * GET /templates/{id}
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findPoolByID(@PathVariable Long id) {
        Optional<PatientPoolTmp> optionalDirector = poolService.findPoolTmpById(id);
        if (optionalDirector.isPresent()) {
            return ResponseEntity.ok(optionalDirector.get()); // Body is PatientPoolTmp
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // No body, 404 Not Found
        }
    }

    // OPTIONAL: Update or partial updates (PUT/PATCH) and Delete
    // For completeness, here's a simple delete example

    /**
     * 4) Delete a Patient
     * DELETE /templates/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePool(@PathVariable Long id) {
        poolService.deletePoolTmpById(id);
        return ResponseEntity.noContent().build(); // No body, 204 No Content
    }
}