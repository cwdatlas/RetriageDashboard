package com.retriage.retriage.enums;

/**
 * Represents the different types of patient pools or areas within an event.
 */
public enum PoolType {
    /**
     * Represents a "Bay" type pool, often associated with initial patient intake or assessment.
     */
    Bay,
    /**
     * Represents a "Medical Service" type pool, for specific medical treatments or consultations.
     */
    MedService,
    /**
     * Represents a "Floor" type pool, indicating a general area or a specific floor within a facility.
     */
    Floor
}