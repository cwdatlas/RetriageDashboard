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
     * Creates a new Patient Pool to be used during an event
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
            logger.info("createPool - Successfully created pool: {}", newPool.getName());
            return ResponseEntity.created(URI.create("/pools/" + newPool.getId())).body(newPool);
        } else {
            logger.error("createPool - Failed to create pool: {}", poolForm.getName());
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of("Failed to create pool."),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "CREATE_POOL_FAILED"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * getAllPools - Returns all Patient Pools
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<PatientPool>> getAllPools() {
        List<PatientPool> pools = poolService.findAllPool();
        logger.info("getAllPools - Returning {} pools", pools.size());
        return ResponseEntity.ok(pools);
    }

    /**
     * findPoolByID - Returns a Pool by ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findPoolByID(@PathVariable Long id) {
        Optional<PatientPool> optionalPool = poolService.findPoolById(id);
        if (optionalPool.isPresent()) {
            logger.info("findPoolByID - Pool with id {} found.", id);
            return ResponseEntity.ok(optionalPool.get());
        } else {
            logger.warn("findPoolByID - Pool with id {} not found.", id);
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of("Pool with id " + id + " not found."),
                    HttpStatus.NOT_FOUND.value(),
                    "POOL_NOT_FOUND"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * deletePool - Deletes a Pool by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePool(@PathVariable Long id) {
        if (poolService.findPoolById(id).isEmpty()) {
            logger.warn("deletePool - Pool with id {} not found.", id);
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of("Pool with id " + id + " not found."),
                    HttpStatus.NOT_FOUND.value(),
                    "POOL_NOT_FOUND"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        poolService.deletePoolById(id);
        logger.info("deletePool - Pool with id {} successfully deleted.", id);
        return ResponseEntity.noContent().build();
    }
}