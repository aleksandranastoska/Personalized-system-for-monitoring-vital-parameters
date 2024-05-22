package finki.veb.personalizedsystemformonitoringvitalparameters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class PersonalizedSystemForMonitoringVitalParametersApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalizedSystemForMonitoringVitalParametersApplication.class, args);
    }

}
