package com.retriage.retriage.models;

import lombok.Data;

/**
 * A generic wrapper class for standardizing API or WebSocket responses.
 * Provides a structure to include HTTP status, an optional error/message, and the actual data payload.
 *
 * @param <T> The type of the data payload contained in the response.
 */
@Data
public class ResponseWrapper<T> {
    /**
     * The HTTP status code of the response (e.g., 200, 400, 404).
     */
    private int httpStatus;
    /**
     * An optional error or message string providing details about the response status.
     * Note: This field is populated using the 'message' parameter in the constructor.
     */
    private String error; // Using 'error' field name, but constructor takes 'message'
    /**
     * The actual data payload of the response, typed according to the generic parameter T.
     * Can be null if there is no data or in case of an error.
     */
    private T data;

    /**
     * Constructs a new {@code ResponseWrapper}.
     *
     * @param httpStatus The HTTP status code for the response.
     * @param message    A message string, typically used for error descriptions or success notifications. This populates the 'error' field.
     * @param data       The data payload to include in the response.
     */
    public ResponseWrapper(int httpStatus, String message, T data) {
        this.httpStatus = httpStatus;
        this.error = message; // Populating 'error' field with 'message' parameter
        this.data = data;
    }
}