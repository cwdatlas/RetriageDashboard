package com.retriage.retriage.forms;

import com.retriage.retriage.models.PatientPoolTmp;
import com.retriage.retriage.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class EventTmpForm {

    @NotBlank(message = "Event name is required")
    private String name;

    @NotNull(message = "End time is required")
    private Long duration;

    @NotNull(message = "Must contain at least one PatientPool")
    @Size(min = 1, message = "Must contain at least one PatientPool")
    private List<PatientPoolTmp> poolTmps;

    public EventTmpForm() {
    }

}