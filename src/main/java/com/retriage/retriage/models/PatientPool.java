package com.retriage.retriage.models;

import com.retriage.retriage.enums.PoolType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Represents a patient pool within an event.
 * This class is a JPA entity mapped to the "patient_pool" table in the database.
 * It defines an area or queue for patients and manages the list of patients currently within it.
 */
@Entity
@Data
@Table(name = "patient_pool")
public class PatientPool {

    /**
     * The unique identifier for the patient pool. This is the primary key and is auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the patient pool (e.g., "Triage Bay 1", "Radiology").
     */
    private String name;

    /**
     * The expected time (in milliseconds) a patient typically spends being processed in this pool.
     */
    private Long processTime;

    /**
     * The timestamp (milliseconds since epoch) when a patient currently being processed in this pool started processing.
     * This is typically used for pools that process one patient at a time.
     */
    private Long startedProcessingAt;

    /**
     * Flag indicating whether patients should be automatically discharged from this pool upon completion of processing.
     */
    private boolean autoDischarge;

    /**
     * The list of {@link Patient}s currently associated with this pool.
     * This is a one-to-many relationship. Operations on the PatientPool will cascade to the Patients.
     * The foreign key is managed by the Patient entity via the 'pool_id' column.
     */
    //Owner
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pool_id")
    private List<Patient> patients;

    /**
     * The type of this patient pool (e.g., Bay, MedService, Floor).
     * See {@link PoolType} enum.
     */
    private PoolType poolType;

    /**
     * The maximum number of patients allowed in the queue for this pool.
     */
    private int queueSize;

    /**
     * An optional string representing an icon associated with this pool type, used for UI representation.
     */
    private String icon;

    /**
     * Default no-argument constructor required by JPA.
     */
    public PatientPool() {
    }
}