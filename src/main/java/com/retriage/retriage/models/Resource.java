package com.retriage.retriage.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int processTime;

    @Getter
    @Setter
    private boolean active;

    @Getter
    @Setter
    private boolean useable;

    //Owner
    @Getter
    @Setter
    @ManyToMany
    private List<Patient> patients;

    public Resource() {
    }
}
