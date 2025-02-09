package com.retriage.retriage.repo;
import com.retriage.retriage.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author John Botonakis
 * @version 1.0
 */


/**
 * To Comment
 */
//public Object save(Patient patient) {
//    //TODO
//}

@Repository //

/**
 * To Comment
 */
public interface PatientRepo extends JpaRepository<Patient, String> {

    Optional<Patient> findByID (String id);//Pass in a string, try to find the user, possible no user exists

//  TODO:
//    Optional<Patient> findByPhone(String phone);
//    Optional<Patient> findByFName(String firstname);
//    Optional<Patient> findByLName(String lastname);
}
