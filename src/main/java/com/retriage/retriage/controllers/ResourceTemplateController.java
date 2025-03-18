package com.retriage.retriage.controllers;

import com.retriage.retriage.forms.ResourceForm;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.services.ResourceTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/resources/templates")
public class ResourceTemplateController {
    private static final Logger log = LoggerFactory.getLogger(ResourceTemplateController.class);
    /**
     *
     */
    private final ResourceTemplateService resourceService;

    /**
     * Constructor injection of the service
     */
    public ResourceTemplateController(ResourceTemplateService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * 1) Create a new PatientPool
     * POST /patients
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public String createResource(@RequestBody ResourceForm resourceForm) {
        //secondary validation

        PatientPool newResource = new PatientPool();
        newResource.setName(resourceForm.getName());
        newResource.setActive(resourceForm.isActive());
        newResource.setUseable(resourceForm.isUseable());
        newResource.setPatients(resourceForm.getPatients());
        newResource.setProcessTime(resourceForm.getProcessTime());

        boolean saved = resourceService.saveResourceTmp(newResource);
        String response = "Unable to save";
        if (saved) {
            response = "Saved Successfully";
            log.debug("createResource: Saved new resource Template name '{}'", newResource.getName());
        } else {
            log.warn("createResource: Unable to save template name'{}'", newResource.getName());
        }
        return response;
    }

    /**
     * 2) Get all Patients
     * GET /patients
     */
    @GetMapping(produces = "application/json")
    public List<PatientPool> getAllResources() {
        List<PatientPool> resources = resourceService.findAllResourcesTmp();
        log.debug("Found {} resources", resources.size());
        log.debug("Resources '{}' found", resources);
        return resources;
    }

    /**
     * 3) Get one Patient by ID
     * GET /patients/{id}
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<PatientPool> findResourceByID(@PathVariable Long id) {
        Optional<PatientPool> optionalDirector = resourceService.findResourceTmpById(id);
        return optionalDirector
                .map(resource -> ResponseEntity.ok(resource))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // OPTIONAL: Update or partial updates (PUT/PATCH) and Delete
    // For completeness, here's a simple delete example

    /**
     * 4) Delete a Patient
     * DELETE /patients/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResourceTmpById(id);
        return ResponseEntity.noContent().build();
    }
}
