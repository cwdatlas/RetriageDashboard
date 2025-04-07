package com.retriage.retriage.controllers;

import com.retriage.retriage.exceptions.ErrorResponse;
import com.retriage.retriage.forms.PatientPoolTmpForm;
import com.retriage.retriage.models.PatientPoolTmp;
import com.retriage.retriage.services.PatientPoolTmpService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> createPool(@Valid @RequestBody PatientPoolTmpForm poolForm) {
        PatientPoolTmp newPool = new PatientPoolTmp();
        newPool.setName(poolForm.getName());
        newPool.setAutoDischarge(poolForm.isAutoDischarge());
        newPool.setProcessTime(poolForm.getProcessTime());
        newPool.setPoolType(poolForm.getPoolType());
        newPool.setQueueSize(poolForm.getQueueSize());
        newPool.setIcon(poolForm.getIcon());

        boolean saved = poolService.savePoolTmp(newPool);
        if (saved) {
            log.info("createPool - Pool template created successfully with ID: {}", newPool.getId());
            return ResponseEntity.created(URI.create("/templates/" + newPool.getId())).body(newPool);
        } else {
            log.error("createPool - Pool template creation failed.");
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of("Unable to save pool template."),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "SAVE_FAILED"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 2) Get all Patients
     * GET /templates
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<PatientPoolTmp>> getAllPools() {
        List<PatientPoolTmp> pools = poolService.findAllPoolTmp();
        log.info("getAllPools - Retrieved {} pool templates.", pools.size());
        return ResponseEntity.ok(pools);
    }

    /**
     * 3) Get one Patient by ID
     * GET /templates/{id}
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findPoolByID(@PathVariable Long id) {
        Optional<PatientPoolTmp> optionalPool = poolService.findPoolTmpById(id);
        if (optionalPool.isPresent()) {
            log.info("findPoolByID - Pool template found with ID: {}", id);
            return ResponseEntity.ok(optionalPool.get());
        } else {
            log.warn("findPoolByID - Pool template find failed: Pool template with id {} not found.", id);
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of("Template with id " + id + " not found."),
                    HttpStatus.NOT_FOUND.value(),
                    "TEMPLATE_NOT_FOUND"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * 4) Delete a Patient
     * DELETE /templates/{id}
     */
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> deletePool(@PathVariable Long id) {
        if (poolService.findPoolTmpById(id).isEmpty()) {
            log.warn("deletePool - Pool template delete failed: Pool template with id {} not found.", id);
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of("Template with id " + id + " not found."),
                    HttpStatus.NOT_FOUND.value(),
                    "POOL_TEMPLATE_NOT_FOUND"
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        poolService.deletePoolTmpById(id);
        log.info("deletePool - Pool template deleted with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}