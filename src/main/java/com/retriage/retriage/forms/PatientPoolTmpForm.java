package com.retriage.retriage.forms;

import com.retriage.retriage.enums.PoolType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PatientPoolTmpForm {

    @NotBlank(message = "PatientPool name is required")
    private String name;

    @NotNull
    private Long processTime;

    @NotNull(message = "Must include true or false for autoDischarge attribute")
    private boolean autoDischarge;

    @NotNull(message = "Must set poolType to either 'Bay' or 'MedicalService'")
    private PoolType poolType;

    @NotNull(message = "Must pass a value of more than 0 into poolNumber")
    @Min(1)
    private int poolNumber;

    @NotNull
    @Min(0)
    private int queueSize;

    private String icon;

    public PatientPoolTmpForm() {
    }
}
