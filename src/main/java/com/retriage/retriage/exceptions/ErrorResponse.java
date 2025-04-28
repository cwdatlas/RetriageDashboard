package com.retriage.retriage.exceptions;

import lombok.Data;

import java.util.List;

/**
 * Represents a standardized error response sent to the client.
 * This class provides a consistent structure for API error responses,
 * including a list of messages, the HTTP status code, and a specific error code.
 */
@Data
public class ErrorResponse {
    /**
     * A list of detailed error messages explaining what went wrong.
     */
    private List<String> messages; // Changed from String to List<String>
    /**
     * The HTTP status code associated with the error (e.g., 400, 404, 500).
     */
    private int statusCode;
    /**
     * A specific, application-defined code representing the type of error.
     */
    private String errorCode;

    /**
     * Constructs a new {@code ErrorResponse}.
     *
     * @param messages   A list of detailed error messages.
     * @param statusCode The HTTP status code for the error.
     * @param errorCode  A specific, application-defined code for the error type.
     */
    public ErrorResponse(List<String> messages, int statusCode, String errorCode) {
        this.messages = messages;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}