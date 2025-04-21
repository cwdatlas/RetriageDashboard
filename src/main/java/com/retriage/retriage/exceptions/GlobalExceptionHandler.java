package com.retriage.retriage.exceptions;

import com.retriage.retriage.forms.PatientForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for the entire application.
 * <p>
 * This class is responsible for handling exceptions that occur across all controllers.
 * It provides a consistent way to manage errors and return meaningful responses to the user,
 * improving error handling and the overall robustness of the application.
 * Currently, it specifically handles {@link MethodArgumentNotValidException} for validation failures,
 * {@link MaxUploadSizeExceededException} for file upload limits, {@link AccessDeniedException} for authorization issues,
 * and a generic {@link Exception} fallback for any other unhandled exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger for this exception handler.
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles any unhandled `Exception` that occurs within the application.
     * This method serves as a fallback for exceptions not specifically handled by other methods.
     * It logs the exception and returns a standardized `ErrorResponse` with an HTTP status of 500 (Internal Server Error).
     *
     * @param ex The uncaught `Exception`.
     * @return A `ResponseEntity` containing a map representing the error details and an
     * HTTP status code of 500. The map includes a timestamp, status code, error message,
     * and the exception's message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Handling generic exception: {}", ex.getClass().getName(), ex); // Logs the exception type and message

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage());

        logger.debug("Returning generic error response: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles exceptions thrown when request body validation fails.
     * <p>
     * This method is invoked when data sent in the request body (e.g., using {@code @RequestBody} with {@code @Valid})
     * does not meet the validation rules defined in the associated class (like {@link PatientForm}).
     * It logs the validation errors and returns a standardized error response with an HTTP status of 400 (Bad Request).
     *
     * @param ex The {@link MethodArgumentNotValidException} that was thrown due to validation failure.
     * @return A {@link ResponseEntity} containing a map representing the error details and an
     * HTTP status code of 400. The map includes a timestamp, status code, a generic validation error message,
     * and the exception's message. Specific field errors are logged internally.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Sets HTTP status to 400
    @ResponseBody // Ensures the response is in the body (e.g., JSON)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Handling validation exception: {} - Field errors: {}",
                ex.getClass().getSimpleName(),
                ex.getBindingResult().getFieldErrors().stream()
                        .map(err -> err.getField() + ": " + err.getDefaultMessage())
                        .collect(Collectors.joining(", ")));


        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Error!");
        errorResponse.put("message", "Data in request body is invalid");

        logger.debug("Returning validation error response: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles exceptions thrown when an uploaded file exceeds the configured maximum size limit.
     * Returns a standardized error response with an HTTP status of 400 (Bad Request).
     *
     * @param ex The {@link MaxUploadSizeExceededException} that was thrown.
     * @return A {@link ResponseEntity} containing a map representing the error details and an
     * HTTP status code of 400. The map includes a timestamp, status code, an error type,
     * and a user-friendly message about the file size limit.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleFileSizeException(MaxUploadSizeExceededException ex) {
        logger.error("Handling file size exceeded exception: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "File too large!");
        errorResponse.put("message", "Please upload a smaller file.");

        logger.debug("Returning file size error response: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles exceptions indicating that a user does not have sufficient permissions to access a resource.
     * Returns a standardized error response with an HTTP status of 403 (Forbidden).
     *
     * @param ex The {@link AccessDeniedException} that was thrown.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} object and an
     * HTTP status code of 403. The error response includes a list of errors,
     * the status code, and a specific error code ("ACCESS_PERM._DENIED").
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Handling access denied exception: {}", ex.getMessage());

        List<String> errors = List.of("Access Denied: You do not have permission to view this page.");
        ErrorResponse errorResponse = new ErrorResponse(errors, HttpStatus.FORBIDDEN.value(), "ACCESS_PERM._DENIED");

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}