package com.retriage.retriage.service;
/**
 * @author John Botonakis
 * @version 1.0
 */

import com.retriage.retriage.domain.Patient;
import com.retriage.retriage.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.retriage.retriage.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class) // Ensure to rollback whenever there is an exception
@RequiredArgsConstructor //Required for dependency injection

public class PatientService {

    private static PatientRepo patientRepo;

    /**
     * Returns all patients, sorted by name in alphabetical order
     * @param page
     * @param size
     * @return
     */
    public Page<Patient> getAllPatients(int page, int size){
        return patientRepo.findAll(PageRequest.of(page,size,Sort.by("name")));
    }

    /**
     * Returns a patient by a specified ID; if doesn't exist, then throw a Patient not Found exception
     * @param id The ID to search by
     * @return The Patient that was searched for, or an error stating the patient cannot be found
     */
    public Patient getPatientById(String id){
        return patientRepo.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    /**
     * Create a new Patient Object
     * @param patient The Patient object to be saved
     */
    public static Patient createPatient(Patient patient){
        return patientRepo.save(patient);
    }

    /**
     * Deletes a Patient Object
     * @param patient The Patient object to be deleted
     */
    public void deletePatient (Patient patient){
        patientRepo.delete(patient);
    }

    /**
     * Associates an uploaded photo with a patient
     * @param id The ID of the patient
     * @param file The actual Photo file to save
     */
    public String uploadPhoto(String id, MultipartFile file) {
//        log.info("Uploading photo with id {}", id); //Slf4j Logging to console
        Patient patient = getPatientById(id); // Gets the patient ID
        String photoURL = photoFunction.apply(id, file); //Sets the URL of the photo  on the Patient Object
        patient.setPhotoURL(photoURL); //Applies the URL to the specific Patient
        patientRepo.save(patient);

        return photoURL;
    }

    /**
     * Takes in a filename, scans to see where the extension separator is ".", returns a fixed file name
     * otherwise, just return ".png" as the default
     */
    final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".")+ 1)).orElse(".png");


    /**
     * Takes in an ID and an Image, ensures there is a directory to save it to, and saves the image.
     *
     */
    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        //Format for saved images: PatientIDPhotoName.fileextention
        String filename = id + fileExtension.apply(image.getOriginalFilename());

        try {
            //Defines a path to save the photo files
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            //If file doesn't exist, it will create a directory, then save it to that location
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            //Save the file, and if it exists already, replace it
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);

            //Returns
            return ServletUriComponentsBuilder.fromCurrentContextPath().path("/patients/image/" + filename).toUriString();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to save Image");
        }
    };
}
