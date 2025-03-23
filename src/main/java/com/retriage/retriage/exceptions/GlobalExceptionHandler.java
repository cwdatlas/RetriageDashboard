package com.retriage.retriage.exceptions;

import com.retriage.retriage.forms.PatientForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  Centralized exception handler for the entire application.
 *  <p>
 *  This class is responsible for handling exceptions that occur across all controllers.
 *  It provides a consistent way to manage errors and return meaningful responses to the user,
 *  improving error handling and the overall robustness of the application.
 *  Currently, it specifically handles {@link MethodArgumentNotValidException} for validation failures.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles any unhandled `Exception` that occurs within the application.
     * This method catches all types of exceptions and returns a standardized
     * `ErrorResponse` with an HTTP status of 500 (Internal Server Error).
     *
     * @param ex The `WebRequest` providing context about the current
     * HTTP request, used here to obtain the request URI.
     * @return A `ResponseEntity` containing the `ErrorResponse` object and an
     * HTTP status code of 500. The `ErrorResponse` includes the
     * timestamp of the error, the HTTP status code and reason phrase,
     * the exception message, and the request URI.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Handling generic exception: {}", ex.getClass().getName(), ex); // Logs the exception type and message

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Something went wrong");
        errorResponse.put("message", ex.getMessage());

        logger.debug("Returning generic error response: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles exceptions thrown when request body validation fails.
     * <p>
     * This method is invoked when data sent in the request body (e.g., using {@code @RequestBody} with {@code @Valid})
     * does not meet the validation rules defined in the associated class (like {@link PatientForm}).
     * It extracts all the validation error messages from the exception and returns them in a structured response.
     *
     * @param ex The {@link MethodArgumentNotValidException} that was thrown due to validation failure.
     * @return ResponseEntity<List<String>> Returns a '400 Bad Request' response.
     * The response body contains a list of error messages,
     * each describing a specific validation failure.
     * This list is intended to provide detailed feedback to the client
     * about what parts of their request were invalid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Sets HTTP status to 400
    @ResponseBody // Ensures the response is in the body (e.g., JSON)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Handling validation exception: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Validation Error!");
        errorResponse.put("message", "Data in request body is invalid");

        logger.debug("Returning validation error response: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleFileSizeException(MaxUploadSizeExceededException ex) {
        logger.warn("Handling file size exceeded exception: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "File too large!");
        errorResponse.put("message", "Please upload a smaller file.");

        logger.debug("Returning file size error response: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}