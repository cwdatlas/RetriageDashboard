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
     * To Comment 27:00
     */
    final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> name.substring(filename.lastIndexOf("."))).orElse(".png");

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
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(id + fileExtension.apply(image.getOriginalFilename())), REPLACE_EXISTING);
        } catch (Exception exception) {
            throw new RuntimeException("Unable to find Image");
        }
        return id;
    };

    /**
     * To Comment
     */
    public String uploadPhoto(String id, MultipartFile file) {
        Patient patient = getPatient(id);
        String photoURL = null;
        patient.setPhotoURL(photoURL);
        PatientRepo.save(patient);

        return photoURL;
    }

    //STOPPED AT 29:34!


}
