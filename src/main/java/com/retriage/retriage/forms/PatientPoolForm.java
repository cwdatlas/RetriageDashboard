package com.retriage.retriage.forms;

import com.retriage.retriage.enums.PoolType;
import com.retriage.retriage.models.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class PatientPoolForm {

    private Long id;

    @NotBlank(message = "PatientPool name is required")
    private String name;

    @Positive(message = "Process time must be a positive number")
    private Long processTime;

    private Long startedProcessingAt;

    private boolean reusable;

    private List<Patient> patients;

    private PoolType poolType;

    private int queueSize;

    public PatientPoolForm() {
    }
}
