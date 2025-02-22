package com.retriage.retriage.models;

import com.retriage.retriage.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
    //TODO update model to match User from the database model

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String firstName;
    @Setter
    private String lastName;
    @Getter
    @Setter
    private Role role;

    public User(){

    }

    public User(String email, String firstName, String lastName, Role role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }


}


