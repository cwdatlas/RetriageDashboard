package com.retriage.retriage.forms;

import com.retriage.retriage.enums.Role;
import com.retriage.retriage.models.Patient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserForm {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String lastName;

    @NotNull(message = "Roles cannot be null")
    @Size(min = 1, message = "At least one role must be assigned")
    private Role role;

    private List<Patient> createdPatients;

    public UserForm() {
    }
}
