package com.retriage.retriage.controllers;

import com.retriage.retriage.exceptions.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@CrossOrigin
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            logger.warn("uploadImage - File upload failed: Empty file.");
            ErrorResponse errorResponse = new ErrorResponse(
                    List.of("Please select a file to upload!"),
                    HttpStatus.BAD_REQUEST.value(),
                    "IMG_FILE_EMPTY"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Generate a unique filename
        String uniqueFilename = UUID.randomUUID() + getFileExtension(file.getOriginalFilename());

        // Ensure the upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        ensureDirectoryExists(uploadPath);

        // Save the file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        logger.info("uploadImage - Image uploaded successfully: {}", uniqueFilename);

        // Return success response
        Map<String, String> successResponse = Map.of(
                "message", "Image uploaded successfully!",
                "filename", uniqueFilename
        );
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    /**
     * Helper method to extract file extension.
     */
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    /**
     * Helper method to ensure the upload directory exists.
     */
    private void ensureDirectoryExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.info("uploadImage - Upload directory created: {}", path);
        }
    }

}