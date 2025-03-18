package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Status;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Must contain at least one Director")
    private User director;

    @Getter
    @Setter
    @NotNull(message = "Must contain at least one Nurse")
    @Size(min = 1, message = "Must contain at least one nurse")
    private List<User> nurses;

    @Getter
    @Setter
    @NotNull(message = "Must contain at least one PatientPool")
    @Size(min = 1, message = "Must contain at least one PatientPool")
    private List<PatientPool> resources;

    @Getter
    @Setter
    @NotBlank(message = "Status is required")
    private Status status;

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
//      In case we decide to use actual time instead of just an Int
//    @NotNull(message = "Start time is required")
//    @FutureOrPresent(message = "Start time must be in the present or future")
//    private LocalDateTime startTime;
//
//    @NotNull(message = "End time is required")
//    @FutureOrPresent(message = "End time must be in the present or future")
//    private LocalDateTime endTime;