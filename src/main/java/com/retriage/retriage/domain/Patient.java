package com.retriage.retriage.domain;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
/**
 * Patient
 * @author John Botonakis
 * @version 1.0
 * @desc This Patient class is responsible for all the information pertaining to
 * a patient that's able to be registered on our system.
 */


@Entity //
@Table(name = "patients") //Refers to the table it will be stored in
@Getter //
@Setter //
@NoArgsConstructor //
@AllArgsConstructor //
@JsonInclude(NON_DEFAULT) //Maps information to JSON only what is not the set default

/**
 * Patient Class
 * @desc: A class representing a Patient for the hospital, to be saved in the database.
 */
public class Patient {
    @Id
    @UuidGenerator
    @Column(name = "ID", unique = true, updatable = false) // Specifies how the patient object gets stored
    private String ID; // Primary key
    private String firstname;
    private String lastname;
    private String phone;
    private String condition;
    private String photoID;

    public Patient(String ID, String firstName, String lastName, String phone, String condition, String photoID) {
    }

    /**
     * To Comment
     */
    public void setPhotoURL(String photoURL) {
        //TODO
    }
}
