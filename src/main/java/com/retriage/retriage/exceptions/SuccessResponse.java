package com.retriage.retriage.exceptions;

public class SuccessResponse {
    private String message;
    private Long eventId;

    public SuccessResponse(String message, Long eventId) {
        this.message = message;
        this.eventId = eventId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
