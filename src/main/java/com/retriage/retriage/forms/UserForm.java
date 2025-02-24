package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.Patient;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserForm {
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private Role role;
    @Getter
    @Setter
    private List<Patient> createdPatients;

    public UserForm() {
    }
}
