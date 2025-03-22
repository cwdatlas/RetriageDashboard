package com.retriage.retriage.exceptions;

import com.retriage.retriage.forms.PatientForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
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
    /**
     * Handles any unhandled `Exception` that occurs within the application.
     * This method catches all types of exceptions and returns a standardized
     * `ErrorResponse` with an HTTP status of 500 (Internal Server Error).
     *
     * @param ex      The `Exception` that was thrown. The message from this
     * exception will be included in the error response.
     * @param request The `WebRequest` providing context about the current
     * HTTP request, used here to obtain the request URI.
     * @return A `ResponseEntity` containing the `ErrorResponse` object and an
     * HTTP status code of 500. The `ErrorResponse` includes the
     * timestamp of the error, the HTTP status code and reason phrase,
     * the exception message, and the request URI.
     */
    @ExceptionHandler(Exception.class) // Catch all exceptions (you can be more specific)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
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
     *                                  The response body contains a list of error messages,
     *                                  each describing a specific validation failure.
     *                                  This list is intended to provide detailed feedback to the client
     *                                  about what parts of their request were invalid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Sets HTTP status to 400
    @ResponseBody // Ensures the response is in the body (e.g., JSON)
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
}
