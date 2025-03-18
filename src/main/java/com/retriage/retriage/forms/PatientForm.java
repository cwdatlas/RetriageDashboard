package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Condition;
import com.retriage.retriage.models.PatientPool;
import com.retriage.retriage.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PatientForm {
    @Getter
    @Setter
    @NotBlank(message = "Card ID is required")
    private String cardId;

    @Getter
    @Setter
    @NotBlank(message = "First name is required")
    private String firstName;

    @Getter
    @Setter
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Getter
    @Setter
    @NotNull(message = "Condition is required")
    private Condition condition;

    @Getter
    @Setter
    private List<PatientPool> resourceList;

    @Getter
    @Setter
    @NotNull(message = "Retriage nurse is required")
    private User retriageNurse;

    public PatientForm() {
    }
}
