package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Condition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatientForm {

    private Long id;

    @NotBlank(message = "Card ID is required")
    private String cardId;

    @NotNull(message = "Condition is required")
    private Condition condition;

    public PatientForm() {
    }
}
