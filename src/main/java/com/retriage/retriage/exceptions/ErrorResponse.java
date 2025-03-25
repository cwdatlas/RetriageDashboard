package com.retriage.retriage.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
/**
 * Represents a standardized error response sent to the client, containing details like timestamp,
 * HTTP status, error type, message, and the request path.
 */
public class ErrorResponse {
    private String message;
    private int statusCode; // Optional: Include the HTTP status code
    private String errorCode; // Optional: A specific error code

    public ErrorResponse(String message, int statusCode, String errorCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public ErrorResponse() {
        // Default constructor
    }

    // Getters and setters (or use Lombok's @Data annotation)

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}