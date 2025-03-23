package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Condition;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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
