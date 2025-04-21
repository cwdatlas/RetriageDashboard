package com.retriage.retriage.models;

import com.retriage.retriage.enums.PoolType;
import jakarta.persistence.*;
import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;

/**
 * Represents a template for creating patient pools.
 * This class is a JPA entity mapped to the "patient_pool_templates" table in the database.
 * It defines the standard characteristics of a pool type that can be used when creating new events.
 */
@Entity
@Data
@Table(name = "patient_pool_templates")
public class PatientPoolTmp {

    /**
     * The unique identifier for the patient pool template. This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the patient pool template. Intended to be unique.
     */
    @Unique // Note: This annotation is from Checker Framework, typically used for static analysis.
    private String name;

    /**
     * The default process time (duration a patient typically spends) in pools created from this template (in milliseconds).
     */
    private Long processTime;

    /**
     * Flag indicating whether patients should be automatically discharged from pools created from this template upon completion.
     */
    private boolean autoDischarge;

    /**
     * The type of patient pool this template represents (e.g., Bay, MedService, Floor).
     * See {@link PoolType} enum.
     */
    private PoolType poolType;

    /**
     * The default number of individual patient pools to create from this template when setting up an event.
     */
    private int poolNumber;

    /**
     * The default maximum number of patients allowed in the queue for pools created from this template.
     */
    private int queueSize;

    /**
     * An optional string representing an icon associated with this template's pool type, used for UI representation.
     */
    private String icon;

    /**
     * Default no-argument constructor required by JPA.
     */
    public PatientPoolTmp() {
    }
}