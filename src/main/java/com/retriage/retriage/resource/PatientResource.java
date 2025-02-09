package com.retriage.retriage.resource;

import com.retriage.retriage.domain.Patient;
import com.retriage.retriage.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.retriage.retriage.constant.Constant.PHOTO_DIRECTORY;

/**
 * PatientResource
 * This class handles HTTP requests for patient-related operations.
 * @Author: John Botonakis
 *
 */

@RestController// Tells Spring Boot that this class is a REST controller (handles HTTP requests/responses).
@RequestMapping("/patients") // Sets the base URL for all endpoints in this class to start with "/patients".
@RequiredArgsConstructor// Lombok annotation to automatically create a constructor for required fields.
public class PatientResource {
    // This field holds the service that contains the business logic for managing patients.
    private  PatientService patientService;

    // Handles HTTP POST requests to create a new patient.
    @PostMapping
    public ResponseEntity<Patient> createPatient (@RequestBody Patient patient) {
        // @RequestBody tells Spring to deserialize the request's JSON body into a Patient object.
        // This line returns a 201 Created response.
        // URI.create("/patients/<user ID>") is a placeholder and should be updated to the new patient's ID.
        // It calls PatientService.createPatient(patient) to create the patient record.
        return ResponseEntity
                .created(URI.create("/patients/<user ID>")) // Sets the "Location" header in the response.
                .body(PatientService.createPatient(patient)); // Puts the created patient object in the response body.
    }

    // Handles HTTP GET requests to retrieve a paginated list of patients.
    @GetMapping // Maps GET requests sent to "/patients" to this method.
    public ResponseEntity<Page<Patient>> getPatients(
            @RequestParam(value = "page", defaultValue = "0") int page,   // Retrieves the "page" query parameter; defaults to 0 if not provided.
            @RequestParam(value = "size", defaultValue = "10") int size) {  // Retrieves the "size" query parameter; defaults to 10 if not provided.
        // Calls the service method to get a page of patients and returns them wrapped in a 200 OK response.
        return ResponseEntity
                .ok() // Sets the HTTP status to 200 OK.
                .body(patientService.getAllPatients(page, size)); // Puts the paginated patient list in the response body.
    }


    // Handles HTTP GET requests to retrieve a specific patient by their ID.
    @GetMapping("/id") // Maps GET requests sent to "/patients/id" to this method.
    public ResponseEntity<Patient> getPatients(@PathVariable(value = "id") String id) {
        // @PathVariable extracts the "id" value from the URL.
        // Calls the service method to find the patient by ID and returns it in a 200 OK response.
        return ResponseEntity
                .ok() // Sets the HTTP status to 200 OK.
                .body(patientService.getPatientById(id)); // Puts the found patient in the response body.
    }

    // Handles HTTP GET requests to upload a photo for a patient.
    @GetMapping("/photo") // Maps GET requests sent to "/patients/photo" to this method.
    public ResponseEntity<String> uploadPhoto(
            @RequestParam(value = "id") String id,       // Retrieves the "id" query parameter to identify the patient.
            @RequestParam("file") MultipartFile file) {   // Retrieves the file from the request (should be a photo).
        // Calls the service method to upload the photo and returns a result (like a URL or message) in a 200 OK response.
        return ResponseEntity
                .ok() // Sets the HTTP status to 200 OK.
                .body(patientService.uploadPhoto(id, file)); // Puts the result of the photo upload in the response body.
    }

    // Handles HTTP GET requests to retrieve a patient's photo as a byte array.
    @GetMapping(path = "/image/{filename}") // Maps GET requests sent to "/patients/image/{filename}" to this method.
    public byte[] getPhoto(@PathVariable(value = "filename") String filename) throws IOException {
        // @PathVariable extracts the "filename" from the URL.
        // Reads the file from the file system:
         return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }
}
