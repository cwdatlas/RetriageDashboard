package com.retriage.retriage.exceptions;

/**
 * Represents a standard structure for returning a successful response from an API endpoint.
 * It typically includes a confirmation message and potentially an identifier related to the success (e.g., a created resource ID).
 */
public class SuccessResponse {
    /**
     * A descriptive message indicating the nature of the success.
     */
    private String message;
    /**
     * An optional identifier related to the successful operation, such as the ID of a newly created event.
     */
    private Long eventId;

    /**
     * Constructs a new {@code SuccessResponse}.
     *
     * @param message A descriptive message indicating the nature of the success.
     * @param eventId An optional identifier related to the successful operation.
     */
    public SuccessResponse(String message, Long eventId) {
        this.message = message;
        this.eventId = eventId;
    }

    /**
     * Gets the success message.
     *
     * @return The success message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the success message.
     *
     * @param message The new success message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the event ID associated with the success, if applicable.
     *
     * @return The event ID, or {@code null} if not applicable.
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID associated with the success.
     *
     * @param eventId The new event ID.
     */
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}