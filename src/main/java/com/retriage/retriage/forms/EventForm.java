package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.Resource;
import com.retriage.retriage.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class EventForm {
    @Getter
    @Setter
    @NotBlank(message = "Event name is required")
    private String name;

    @Getter
    @Setter
    @NotBlank(message = "Director is required")
    private User director;

    @Getter
    @Setter
    @NotNull(message = "Must contain at least one nurse")
    private List<User> nurses;

    @Getter
    @Setter
    @NotNull(message = "Must contain at least one Resource")
    private List<Resource> resources;

    @Getter
    @Setter
    @NotBlank(message = "Status is required")
    private Status status;

//    @NotNull(message = "Start time is required")
//    @FutureOrPresent(message = "Start time must be in the present or future")
//    private LocalDateTime startTime;
//
//    @NotNull(message = "End time is required")
//    @FutureOrPresent(message = "End time must be in the present or future")
//    private LocalDateTime endTime;

    @Getter
    @Setter
    @NotNull(message = "Start time is required")
    private int startTime;

    @Getter
    @Setter
    @NotNull(message = "End time is required")
    private int endTime;

    public EventForm() {
    }

}
