package com.retriage.retriage.controllers;

import com.retriage.retriage.forms.ResourceForm;
import com.retriage.retriage.models.Resource;
import com.retriage.retriage.services.ResourceTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/resources/templates")
public class ResourceTemplateController {
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
     * 1) Create a new Resource
     * POST /patients
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public String createResource(@RequestBody ResourceForm resourceForm) {
        //secondary validation

        Resource newResource = new Resource();
        newResource.setName(resourceForm.getName());
        newResource.setActive(resourceForm.isActive());
        newResource.setUseable(resourceForm.isUseable());
        newResource.setPatients(resourceForm.getPatients());
        newResource.setProcessTime(resourceForm.getProcessTime());
        newResource.setParentEvent(resourceForm.getParentEvent());

        boolean saved = resourceService.saveResourceTmp(newResource);
        String response = "Unable to save";
        if (saved) {
            response = "Saved Successfully";
        }
        return response;
    }

    /**
     * 2) Get all Patients
     * GET /patients
     */
    @GetMapping(produces = "application/json")
    public List<Resource> getAllResources() {
        return resourceService.findAllResourcesTmp();
    }

    /**
     * 3) Get one Patient by ID
     * GET /patients/{id}
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Resource> findResourceByID(@PathVariable Long id) {
        Optional<Resource> optionalDirector = resourceService.findResourceTmpById(id);
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
