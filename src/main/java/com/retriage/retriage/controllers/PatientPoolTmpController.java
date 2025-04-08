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

@RestController
@CrossOrigin
@RequestMapping("/api/pools/templates")
public class PatientPoolTmpController {
    private static final Logger logger = LoggerFactory.getLogger(PatientPoolTmpController.class);
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
            logger.info("createPool - Pool template created successfully with ID: {}", newPool.getId());
            return ResponseEntity.created(URI.create("/templates/" + newPool.getId())).body(newPool);
        } else {
            logger.error("createPool - Pool template creation failed.");
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
        logger.info("getAllPools - Retrieved {} pool templates.", pools.size());
        return ResponseEntity.ok(pools);
    }

    /**
     * 3) Get one Patient by ID
     * GET /templates/{id}
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> findPoolByID(@PathVariable Long id) {
        PatientPoolTmp optionalPool = poolService.findPoolTmpById(id);
        if (optionalPool != null) {
            logger.info("findPoolByID - Pool template found with ID: {}", id);
            return ResponseEntity.ok(optionalPool);
        } else {
            logger.warn("findPoolByID - Pool template find failed: Pool template with id {} not found.", id);
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
        if (poolService.findPoolTmpById(id) == null) {
            logger.warn("deletePool - Pool template delete failed: Pool template with id {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        poolService.deletePoolTmpById(id);
        if (poolService.findPoolTmpById(id) != null) {
            logger.info("deletePoolTmp - PoolTmp failed to delete with id: {}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {
            logger.info("deletePoolTmp - PoolTmp deleted with id: {}", id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}