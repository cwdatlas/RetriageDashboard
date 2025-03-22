package com.retriage.retriage.controllers;

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
import java.util.UUID;

@Controller
@CrossOrigin
public class ImageController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload!", HttpStatus.BAD_REQUEST);
        }

        try {
            // Generate a filename for that saved image
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID() + fileExtension;

            // Save the image
            Path filePath = Paths.get(uploadDir, uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            return new ResponseEntity<>("Image uploaded successfully. Filename: " + uniqueFilename, HttpStatus.OK);

        } catch (IOException e) {
            // Add logger here

            return new ResponseEntity<>("Failed to upload image!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}