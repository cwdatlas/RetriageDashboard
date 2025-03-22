package com.retriage.retriage.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
/**
 * Represents a standardized error response sent to the client, containing details like timestamp,
 * HTTP status, error type, message, and the request path.
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path; // Optional: The request path that caused the error
}