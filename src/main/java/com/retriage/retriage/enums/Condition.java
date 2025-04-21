package com.retriage.retriage.enums;

/**
 * Represents the possible medical condition categories or triage levels for a patient.
 * These typically indicate the severity of a patient's injury or illness and the urgency of care required.
 */
public enum Condition {
    /**
     * Indicates a patient with minor injuries, who can wait for care. (Often Green in standard triage)
     */
    Minor,
    /**
     * Indicates a patient with injuries that require care but can be delayed. (Often Yellow in standard triage)
     */
    Delayed,
    /**
     * Indicates a patient requiring immediate life-saving intervention. (Often Red in standard triage)
     */
    Immediate,
    /**
     * Indicates a patient who is deceased. (Often Black in standard triage)
     */
    Deceased
}