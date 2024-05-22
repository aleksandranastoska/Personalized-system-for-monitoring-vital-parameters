package finki.veb.personalizedsystemformonitoringvitalparameters.repository;

import finki.veb.personalizedsystemformonitoringvitalparameters.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {

}
