package com.retriage.retriage.models;

import com.retriage.retriage.enums.PoolType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "patient_pool")
public class PatientPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long processTime;

    private boolean active;

    private boolean useable;
    //Owner
    @ManyToMany
    private List<Patient> patients;

    private PoolType poolType;

    public PatientPool() {
    }
}
