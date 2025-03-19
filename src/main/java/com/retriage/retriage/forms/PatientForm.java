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

    @NotBlank(message = "Card ID is required")
    private String cardId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Condition is required")
    private Condition condition;

    private List<PatientPool> poolList;

    @NotNull(message = "Retriage nurse is required")
    private User retriageNurse;

    public PatientForm() {
    }
}
