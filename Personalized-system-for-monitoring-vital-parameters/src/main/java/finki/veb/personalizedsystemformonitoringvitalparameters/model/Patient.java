package finki.veb.personalizedsystemformonitoringvitalparameters.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    private String embg;
    private Date dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String name;
    private String surname;
}
