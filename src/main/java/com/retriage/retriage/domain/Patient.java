package com.retriage.retriage.domain;
/**
 * @author John Botonakis
 * @version 1.0
 */

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


@Entity //
@Table(name = "patients") //
@Getter //
@Setter //
@NoArgsConstructor //
@AllArgsConstructor //
@JsonInclude(NON_DEFAULT) //
/**
 * To Comment
 */
public class Patient {
    @Id
    @UuidGenerator
    @Column(name = "ID", unique = true, updatable = false)
    private String ID;
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
