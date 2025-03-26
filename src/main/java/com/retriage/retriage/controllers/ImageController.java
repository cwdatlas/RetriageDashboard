package com.retriage.retriage.controllers;

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
import java.util.UUID;

@Controller
@CrossOrigin
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/uploadImage")
    public RedirectView uploadImage(@RequestParam("image") MultipartFile file) {
        logger.info("Entering uploadImage with file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("uploadImage - Received empty file for upload.");
            RedirectView redirectView = new RedirectView("/upload-form?error=emptyfile"); // Adjust path as needed
            return redirectView;
        }

        try {
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
            Files.copy(file.getInputStream(), filePath);
            logger.info("uploadImage - Image uploaded successfully. Filename: {}", uniqueFilename);

            // Redirect to the homepage
            RedirectView redirectView = new RedirectView("/"); // Assuming "/" is your homepage path
            return redirectView;
        } catch (IOException e) {
            logger.error("uploadImage - Error saving image:", e);
            // Redirect to an error page
            RedirectView redirectView = new RedirectView("/error"); // Adjust path as needed
            return redirectView;
        } finally {
            logger.info("Exiting uploadImage");
        }
    }
}