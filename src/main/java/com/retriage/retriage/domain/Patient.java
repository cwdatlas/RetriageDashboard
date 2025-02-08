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
 * @author John Botonakis
 * @version 1.0
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

    public Patient(String ID, String firstName, String lastName, String email, String phone) {
    }

    /**
     * To Comment
     */
    public void setPhotoURL(String photoURL) {
        //TODO
    }
}
