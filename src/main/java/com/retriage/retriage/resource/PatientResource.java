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

/**
 * @Author: John Botonakis
 *
 */

/**
 * PatientResource
 */
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientResource {
    private  PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> createPatient (@RequestBody Patient patient) {
//        return ResponseEntity.ok(patientService.createPatient(patient));
        return ResponseEntity.created(URI.create("/patients/<user ID>")).body(PatientService.createPatient(patient));
    }

    @GetMapping
    public ResponseEntity<Page<Patient>> getPatients(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10")int size){
        return ResponseEntity.ok().body(patientService.getAllPatients(page, size));
    }


    @GetMapping("/id")
    public ResponseEntity<Patient> getPatients(@PathVariable(value = "id")String id) {
        return ResponseEntity.ok().body(patientService.getPatientById(id));
    }

    @GetMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam(value = "id")String id,
                                              @RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok().body(patientService.uploadPhoto(id, file));
    }

    @GetMapping(path = "/image/{filename}")
    public byte[] getPhoto(@PathVariable(value = "filename")String filename) throws IOException {
//        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
        return null;
    }
}
