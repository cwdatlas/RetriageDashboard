package com.retriage.retriage.forms;

import com.retriage.retriage.enums.PoolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientPoolTmpForm {

    @NotBlank(message = "PatientPool name is required")
    private String name;

    @NotNull
    private Long processTime;

    @NotNull(message = "Must include true or false for useable attribute")
    private boolean useable;

    @NotNull(message = "Must set poolType to either 'Bay' or 'MedicalService'")
    private PoolType poolType;

    @NotBlank(message = "Must pass a value of more than 0 into poolNumber")
    @NotNull(message = "Must pass a value of more than 0 into poolNumber")
    @Size(min = 1, message = "Must have at least 1 pool and a maximum of 5 pools")
    private int poolNumber;

    @NotNull
    @Size(max = 50)
    private int queueSize;

    public PatientPoolTmpForm() {
    }
}
