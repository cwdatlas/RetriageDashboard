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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing {@link PatientPoolTmp} resources.
 * Provides API endpoints for creating, retrieving, and deleting patient pool templates.
 * Handles cross-origin requests via {@link CrossOrigin}.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/pools/templates")
public class PatientPoolTmpController {
    private static final Logger logger = LoggerFactory.getLogger(PatientPoolTmpController.class);
    /**
     * The service responsible for handling PatientPoolTmp business logic.
     */
    private final PatientPoolTmpService poolService;

    /**
     * Constructs an instance of {@code PatientPoolTmpController}.
     *
     * @param poolService The service for managing patient pool templates.
     */
    public PatientPoolTmpController(PatientPoolTmpService poolService) {
        this.poolService = poolService;
    }

    /**
     * Create a new PatientPool template.
     * Handles POST requests to {@code /api/pools/templates}.
     * Validates the input form and saves the new patient pool template.
     * Only accessible to users with the 'Director' role.
     *
     * @param poolForm The form containing the data for the new patient pool template.
     * @return A {@link ResponseEntity} indicating the result of the creation.
     * Returns HTTP 201 (Created) with the created {@link PatientPoolTmp} on success,
     * or HTTP 500 (Internal Server Error) with an {@link ErrorResponse} if saving fails.
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasRole('Director')") // Restricts to Director roles only
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
            // Assuming the URI for a created pool template would be /api/pools/templates/{id}
            return ResponseEntity.created(URI.create("/api/pools/templates/" + newPool.getId())).body(newPool);
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
     * Get all Patient Pool templates.
     * Handles GET requests to {@code /api/pools/templates}.
     * Retrieves and returns a list of all existing patient pool templates.
     * Only accessible to users with the 'Director' role.
     *
     * @return A {@link ResponseEntity} containing a list of all {@link PatientPoolTmp} objects,
     * or an empty list if none exist, with HTTP 200 (OK).
     */
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasRole('Director')") // Restricts to Director roles only
    public ResponseEntity<List<PatientPoolTmp>> getAllPools() {
        List<PatientPoolTmp> pools = poolService.findAllPoolTmp();
        logger.info("getAllPools - Retrieved {} pool templates.", pools.size());
        return ResponseEntity.ok(pools);
    }

    /**
     * Delete a Patient Pool template by its ID.
     * Handles DELETE requests to {@code /api/pools/templates/{id}}.
     * Only accessible to users with the 'Director' role.
     *
     * @param id The ID of the patient pool template to delete.
     * @return A {@link ResponseEntity} indicating the result of the deletion.
     * Returns HTTP 200 (OK) on successful deletion,
     * HTTP 404 (Not Found) if the template does not exist,
     * or HTTP 500 (Internal Server Error) if deletion fails after finding the template.
     */
    @DeleteMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasRole('Director')") // Restricts to Director roles only
    public ResponseEntity<?> deletePool(@PathVariable Long id) {
        if (poolService.findPoolTmpById(id) == null) {
            logger.warn("deletePool - Pool template delete failed: Pool template with id {} not found.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        poolService.deletePoolTmpById(id);
        // Verify deletion
        if (poolService.findPoolTmpById(id) != null) {
            logger.info("deletePoolTmp - PoolTmp failed to delete with id: {}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {
            logger.info("deletePoolTmp - PoolTmp deleted with id: {}", id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}