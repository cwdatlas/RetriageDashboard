package com.retriage.retriage.repo;


import com.retriage.retriage.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepo extends JpaRepository<Patient, String> {

    Optional<Patient> findByID(String id);
    Optional <Patient> findByPhone(String phone);
    Optional <Patient> findByFName(String firstname);
    Optional <Patient> findByLName(String lastname);

}

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
//In case the tutorial fails, there is a setup link through Spring.io to setup MySQL using THIS syntax
//public interface PatientRepo extends CrudRepository<Patient, String> {
//    Optional<Patient> findByID(String id);
//    Optional <Patient> findByPhone(String phone);
//    Optional <Patient> findByFName(String firstname);
//    Optional <Patient> findByLName(String lastname);
//}