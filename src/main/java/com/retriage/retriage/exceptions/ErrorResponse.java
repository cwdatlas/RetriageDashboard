package com.retriage.retriage.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import java.util.List;

/**
 * Represents a standardized error response sent to the client, containing details like timestamp,
 * HTTP status, error type, message, and the request path.
 */
@Data
public class ErrorResponse {
    private List<String> messages; // Changed from String to List<String>
    private int statusCode;
    private String errorCode;

    public ErrorResponse(List<String> messages, int statusCode, String errorCode) {
        this.messages = messages;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}