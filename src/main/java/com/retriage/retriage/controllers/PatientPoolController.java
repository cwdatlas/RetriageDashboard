package com.retriage.retriage.controllers;

import com.retriage.retriage.forms.PatientPoolForm;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.services.PatientPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        logger.info("Entering PatientPoolController constructor with poolService: {}", poolService);
        this.poolService = poolService;
        logger.info("Exiting PatientPoolController constructor");
    }

    /**
     * createPool
     * Creates a Pool to be used during an event
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public String createPool(@RequestBody PatientPoolForm poolForm) {
        logger.info("Entering createPool with poolForm: {}", poolForm);
        //secondary validation

        PatientPool newPool = new PatientPool();
        newPool.setId(poolForm.getId());
        newPool.setName(poolForm.getName());
        newPool.setActive(poolForm.isActive());
        newPool.setUseable(poolForm.isUseable());
        newPool.setPatients(poolForm.getPatients());
        newPool.setProcessTime(poolForm.getProcessTime());
        newPool.setPoolType(poolForm.getPoolType());
        logger.debug("createPool - Created PatientPool object from form: {}", newPool);

        boolean saved = poolService.savePool(newPool);
        logger.info("createPool - Pool saved successfully: {}", saved);
        String response = "Unable to save";
        if (saved) {
            response = "Saved Successfully";
        }
        logger.info("Exiting createPool, returning response: {}", response);
        return response;
    }

    /**
     * getAllPools
     * Returns all previously created Pool objects
     */
    @GetMapping(produces = "application/json")
    public List<PatientPool> getAllPools() {
        logger.info("Entering getAllPools");
        List<PatientPool> pools = poolService.findAllPool();
        logger.info("Exiting getAllPools, returning {} pools", pools.size());
        return pools;
    }

    /**
     * findPoolByID
     * Returns the Pool object associated with the passed in ID
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<PatientPool> findPoolByID(@PathVariable Long id) {
        logger.info("Entering findPoolByID with id: {}", id);
        Optional<PatientPool> optionalPool = poolService.findPoolById(id);
        ResponseEntity<PatientPool> response = optionalPool
                .map(pool -> {
                    logger.info("findPoolByID - Found pool with id: {}", id);
                    return ResponseEntity.ok(pool);
                })
                .orElseGet(() -> {
                    logger.warn("findPoolByID - Pool with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
        logger.info("Exiting findPoolByID, returning response: {}", response.getStatusCode());
        return response;
    }

    /**
     * deletePool
     * Deletes the Pool object associated with the passed in ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePool(@PathVariable Long id) {
        logger.info("Entering deletePool with id: {}", id);
        poolService.deletePoolById(id);
        logger.info("Exiting deletePool, pool with id {} deleted", id);
        return ResponseEntity.noContent().build();
    }
}