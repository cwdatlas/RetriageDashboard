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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Patient> getCreatedPatients() {
        return createdPatients;
    }

    public void setCreatedPatients(List<Patient> createdPatients) {
        this.createdPatients = createdPatients;
    }

    public UserForm() {
    }
}
