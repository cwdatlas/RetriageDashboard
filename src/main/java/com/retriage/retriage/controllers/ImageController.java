package com.retriage.retriage.controllers;

import com.retriage.retriage.exceptions.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

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
            logger.warn("uploadImage - Received empty file for upload.");
            ErrorResponse errorResponse = new ErrorResponse(List.of("Please select a file to upload!"), HttpStatus.BAD_REQUEST.value(), "FILE_EMPTY");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Body is ErrorResponse
        }

        // Generate a filename for that saved image
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID() + fileExtension;
        logger.debug("uploadImage - Generated unique filename: {}", uniqueFilename);

        // Resolve the upload directory path
        Path uploadPath = Paths.get(uploadDir);
        logger.debug("uploadImage - Resolved upload directory path: {}", uploadPath);

        // Create the directory if it doesn't exist
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("uploadImage - Created upload directory: {}", uploadPath);
        }

        // Save the image
        Path filePath = uploadPath.resolve(uniqueFilename);
        logger.debug("uploadImage - Resolved file path for saving: {}", filePath);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        logger.info("uploadImage - Image uploaded successfully. Filename: {}", uniqueFilename);

        // Return a success ResponseEntity
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Image uploaded successfully!");
        successResponse.put("filename", uniqueFilename);
        return new ResponseEntity<>(successResponse, HttpStatus.OK); // Body is Map<String, String>
    }
}