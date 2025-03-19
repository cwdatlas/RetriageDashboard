package com.retriage.retriage.forms;

import com.retriage.retriage.enums.PoolType;
import com.retriage.retriage.models.Patient;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class PatientPoolTmpForm {

    @NotBlank(message = "PatientPool name is required")
    private String name;

    @NotNull
    private int processTime;

    @NotNull(message = "Must include true or false for useable attribute")
    private boolean useable;

    @NotNull(message = "Must set poolType to either 'Bay' or 'MedicalService'")
    private PoolType poolType;

    @NotBlank(message = "Must pass a value of more than 0 into poolNumber")
    @NotNull(message = "Must pass a value of more than 0 into poolNumber")
    @Size(min = 1, message = "Must have at least 1 pool and a maximum of 5 pools")
    private int poolNumber;

    public PatientPoolTmpForm() {
    }
}
