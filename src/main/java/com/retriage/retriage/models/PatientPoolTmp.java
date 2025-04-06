package com.retriage.retriage.models;

import com.retriage.retriage.enums.PoolType;
import jakarta.persistence.*;
import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;

@Entity
@Data
@Table(name = "patient_pool_templates")
public class PatientPoolTmp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Unique
    private String name;

    private Long processTime;

    private boolean useable;

    private PoolType poolType;

    private int poolNumber;

    private int queueSize;

    public PatientPoolTmp() {
    }
}
