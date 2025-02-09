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

@Service // Marks this class as a service, which holds business logic, for Spring to manage.
@Slf4j // Provides a logger named 'log' for logging messages.
@Transactional(rollbackOn = Exception.class) // Ensures that any exception thrown in this class will cause the transaction to rollback.
@RequiredArgsConstructor // Generates a constructor for required fields to facilitate dependency injection.

public class PatientService {
    // The repository that provides CRUD operations for Patient objects.
    private static PatientRepo patientRepo;

    /**
     * Returns all patients, sorted by name in alphabetical order.
     *
     * @param page The current page number (for pagination).
     * @param size The number of patients to return per page.
     * @return A page (a subset) of patients sorted by name.
     */
    public Page<Patient> getAllPatients(int page, int size) {
        // Create a pagination request: page number, page size, and sort by the "name" field.
        return patientRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }


    /**
     * Returns a patient by a specified ID.
     * If the patient doesn't exist, then a RuntimeException is thrown.
     *
     * @param id The ID to search for.
     * @return The Patient object that matches the given ID.
     */
    public Patient getPatientById(String id) {
        // Attempt to find the patient by ID; if not found, throw an exception with a helpful message.
        return patientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }


    /**
     * Create a new Patient object and save it in the repository.
     *
     * @param patient The Patient object to be saved.
     * @return The saved Patient object.
     */
    public static Patient createPatient(Patient patient) {
        // Save the patient to the repository and return the saved instance.
        return patientRepo.save(patient);
    }

    /**
     * Deletes a given Patient object from the repository.
     *
     * @param patient The Patient object to be deleted.
     */
    public void deletePatient(Patient patient) {
        // Remove the patient record from the repository (and thus the database).
        patientRepo.delete(patient);
    }

    /**
     * Associates an uploaded photo with a patient.
     *
     * @param id   The ID of the patient.
     * @param file The photo file to be uploaded.
     * @return The URL where the uploaded photo can be accessed.
     */
    public String uploadPhoto(String id, MultipartFile file) {
        // Retrieve the patient by ID; if not found, an exception is thrown.
        Patient patient = getPatientById(id);

        // Use the photoFunction to process the file and generate a URL for the photo.
        String photoURL = photoFunction.apply(id, file);

        // Set the generated photo URL on the patient object.
        patient.setPhotoURL(photoURL);

        // Save the updated patient record back to the repository.
        patientRepo.save(patient);

        // Return the photo URL to indicate where the photo can be accessed.
        return photoURL;
    }

    /**
     * Function to extract the file extension from a filename.
     * If the filename contains a dot, returns the extension (e.g., ".jpg"); otherwise, defaults to ".png".
     */
    final Function<String, String> fileExtension = filename ->
            Optional.of(filename)                   // Wrap the filename in an Optional.
                    .filter(name -> name.contains("."))  // Check if the filename contains a dot.
                    .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)) // Extract and format the extension.
                    .orElse(".png");              // If no dot is found, default to ".png".

    /**
     * Function to save a patient's photo and return the URL where the photo is accessible.
     * It takes a patient ID and a MultipartFile (the image) as input.
     */
    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        // Build a filename using the patient ID and the file extension of the uploaded image.
        String filename = id + fileExtension.apply(image.getOriginalFilename());

        try {
            // Define the directory where photo files will be stored.
            // PHOTO_DIRECTORY is defined in constant/Constant
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();

            // If the directory does not exist, create it (and any parent directories needed).
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }

            // Save (copy) the uploaded file to the target directory.
            // If a file with the same name exists, it will be replaced.
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);

            // Build a URL that points to the saved image.
            // This uses the current web context and appends the path to the image.
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/patients/image/" + filename)
                    .toUriString();
        } catch (Exception exception) {
            // If any error occurs during file saving, throw a RuntimeException with an error message.
            throw new RuntimeException("Unable to save Image");
        }
    };
}
