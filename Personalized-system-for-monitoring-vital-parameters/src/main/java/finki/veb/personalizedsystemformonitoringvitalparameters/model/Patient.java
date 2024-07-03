package finki.veb.personalizedsystemformonitoringvitalparameters.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients")
public class Patient {
    @Id
    private String Id;
    private String embg;
    private Date dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int age;
    private String name;
    private String surname;
}
