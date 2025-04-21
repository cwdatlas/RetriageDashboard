package com.retriage.retriage.enums;

/**
 * Represents the different roles or levels of access a user can have within the application.
 */
public enum Role {
    /**
     * Represents a user with the role of a Nurse, likely with permissions related to patient care or event management.
     */
    Nurse,
    /**
     * Represents a user with limited or guest access, possibly for viewing information only.
     */
    Guest,
    /**
     * Represents a user with the highest level of access, likely administrative or oversight permissions.
     */
    Director
}