package com.retriage.retriage.service;
/**
 * @author John Botonakis
 * @version 1.0
 */

import com.retriage.retriage.domain.Patient;
import com.retriage.retriage.repo.PatientRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepo patientRepo;
    /**
     * To Comment
     */
    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        try {
            Path fileStorageLocation = Paths.get("").toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {
                //If file doesn't exist, it will save it to a specified location
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(id + ".png"), REPLACE_EXISTING);
        } catch (Exception exception) {
            throw new RuntimeException("Unable to find Image");
        }
    }

    /**
     * To Comment
     */
    public String uploadPhoto(String id, MultipartFile file) {
        Patient patient = getPatient(id);
        String photoURL = null;
        patient.setPhotoURL(photoURL);
        PatientRepo.save(patient);

    /**
     * To Comment
     */
    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> name.substring(filename.lastIndexOf("."))).orElse(".png");

    /**
     * To Comment
     */
    public Page<Patient> getAllPatients(int page, int size) {
        return PatientRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    /**
     * To Comment
     */
    public Patient getPatient (String id) {
        return patientRepo.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    /**
     * To Comment
     */
    public Object createPatient(Patient patient) {
        return PatientRepo.save(patient);
    }

    /**
     * To Comment
     */
    public void deletePatient(Patient patient) {
        //Assign later
    }       return photoURL;
    }

    //STOPPED AT 27:32!
}
