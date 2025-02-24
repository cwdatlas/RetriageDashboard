package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Condition;
import com.retriage.retriage.models.Resource;
import com.retriage.retriage.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PatientForm {
    @Getter
    @Setter
    private String cardId;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private Condition condition;
    @Getter
    @Setter
    private List<Resource> resourceList;
    @Getter
    @Setter
    private User retriageNurse;

    public PatientForm() {
    }
}
