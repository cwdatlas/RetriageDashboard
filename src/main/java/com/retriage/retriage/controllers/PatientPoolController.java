package com.retriage.retriage.controllers;

import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.forms.PatientPoolForm;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.services.PatientPoolService;
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
@RequestMapping("/api/pools")
public class PatientPoolController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final PatientPoolService poolService;

    /**
     * Constructor injection of the service
     */
    public PatientPoolController(PatientPoolService poolService) {
        this.poolService = poolService;
    }

    /**
     * createPool
     * Creates a Pool to be used during an event
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createPool(@RequestBody PatientPoolForm poolForm) {
        PatientPool newPool = new PatientPool();
        newPool.setId(poolForm.getId());
        newPool.setName(poolForm.getName());
        newPool.setActive(poolForm.isActive());
        newPool.setUseable(poolForm.isUseable());
        newPool.setPatients(poolForm.getPatients());
        newPool.setProcessTime(poolForm.getProcessTime());
        newPool.setPoolType(poolForm.getPoolType());

        boolean saved = poolService.savePool(newPool);
        if (saved) {
            return ResponseEntity.created(URI.create("/pools/" + newPool.getId())).body(newPool);
        } else {
            ErrorResponse errorResponse = new ErrorResponse(List.of("Failed to create pool."), HttpStatus.INTERNAL_SERVER_ERROR.value(), "CREATE_FAILED");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * getAllPools
     * Returns all previously created Pool objects
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<PatientPool>> getAllPools() {
        List<PatientPool> pools = poolService.findAllPool();
        return new ResponseEntity<>(pools, HttpStatus.OK);
    }

    /**
     * findPoolByID
     * Returns the Pool object associated with the passed in ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findPoolByID(@PathVariable Long id) {
        Optional<PatientPool> optionalPool = poolService.findPoolById(id);
        if (optionalPool.isPresent()) {
            return ResponseEntity.ok(optionalPool.get()); // Body is PatientPool
        } else {
            ErrorResponse errorResponse = new ErrorResponse(List.of("Pool with id " + id + " not found."), HttpStatus.NOT_FOUND.value(), "POOL_NOT_FOUND");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // Body is ErrorResponse
        }
    }

    /**
     * deletePool
     * Deletes the Pool object associated with the passed in ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePool(@PathVariable Long id) {
        poolService.deletePoolById(id);
        return ResponseEntity.noContent().build();
    }
}