package com.retriage.retriage.controllers;

import com.retriage.retriage.forms.PatientPoolForm;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.services.PatientPoolTmpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * POST /patients
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public String createPool(@RequestBody PatientPoolForm poolForm) {
        //secondary validation

        PatientPool newPool = new PatientPool();
        newPool.setId(poolForm.getId());
        newPool.setName(poolForm.getName());
        newPool.setActive(poolForm.isActive());
        newPool.setUseable(poolForm.isUseable());
        newPool.setPatients(poolForm.getPatients());
        newPool.setProcessTime(poolForm.getProcessTime());
        newPool.setPoolType(poolForm.getPoolType());

        boolean saved = poolService.savePoolTmp(newPool);
        String response = "Unable to save";
        if (saved) {
            response = "Saved Successfully";
            log.debug("createPool: Saved new pool Template name '{}'", newPool.getName());
        } else {
            log.warn("createPool: Unable to save template name'{}'", newPool.getName());
        }
        return response;
    }

    /**
     * 2) Get all Patients
     * GET /patients
     */
    @GetMapping(produces = "application/json")
    public List<PatientPool> getAllPools() {
        List<PatientPool> pools = poolService.findAllPoolTmp();
        log.debug("Found {} pools", pools.size());
        log.debug("Pools '{}' found", pools);
        return pools;
    }

    /**
     * 3) Get one Patient by ID
     * GET /patients/{id}
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<PatientPool> findPoolByID(@PathVariable Long id) {
        Optional<PatientPool> optionalDirector = poolService.findPoolTmpById(id);
        return optionalDirector
                .map(pool -> ResponseEntity.ok(pool))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // OPTIONAL: Update or partial updates (PUT/PATCH) and Delete
    // For completeness, here's a simple delete example

    /**
     * 4) Delete a Patient
     * DELETE /patients/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePool(@PathVariable Long id) {
        poolService.deletePoolTmpById(id);
        return ResponseEntity.noContent().build();
    }
}
