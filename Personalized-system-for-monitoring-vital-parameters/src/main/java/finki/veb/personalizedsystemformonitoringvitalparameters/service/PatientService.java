package finki.veb.personalizedsystemformonitoringvitalparameters.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import finki.veb.personalizedsystemformonitoringvitalparameters.model.Gender;
import finki.veb.personalizedsystemformonitoringvitalparameters.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    @Autowired
    private InfluxDBClient influxDBClient;
//    public List<Patient> getAllPatients() {
//        String query = "from(bucket: \"proekt\") |> range(start: -24h) |> filter(fn: (r) => r[\"_measurement\"] == \"patient1\")";
//        QueryApi queryApi = influxDBClient.getQueryApi();
//        return queryApi.query(query);
//    }
//
//    public Optional<Patient> getPatientDetails(String patientId) {
//        String query = "from(bucket: \"proekt\") |> range(start: -24h) |> filter(fn: (r) => r[\"patientId\"] == \"" + patientId + "\")";
//        QueryApi queryApi = influxDBClient.getQueryApi();
//        return queryApi.query(query).getFirst();
//    }
//    InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);
public List<Patient> getAllPatients() {
    String flux = "from(bucket: \"proekt\")\n" +
            "  |> range(start: -24h)\n" +
            "  |> filter(fn: (r) => r._measurement == \"patient\")";
    QueryApi queryApi = influxDBClient.getQueryApi();
    List<FluxTable> tables = queryApi.query(flux);

    List<Patient> patients = new ArrayList<>();
    for (FluxTable table : tables) {
        for (FluxRecord record : table.getRecords()) {
            Patient patient = new Patient();

            Object patientId = record.getValueByKey("patientId");
            if (patientId != null) {
                patient.setId(patientId.toString());
            }

            Object embg = record.getValueByKey("embg");
            if (embg != null) {
                patient.setEmbg(embg.toString());
            }

            Object dateOfBirth = record.getValueByKey("dateOfBirth");
            if (dateOfBirth != null) {
                patient.setDateOfBirth(new Date(Long.parseLong(dateOfBirth.toString())));
            }

            Object age = record.getValueByKey("age");
            if (age != null) {
                patient.setAge(Integer.parseInt(age.toString()));
            }

            Object name = record.getValueByKey("name");
            if (name != null) {
                patient.setName(name.toString());
            }

            Object surname = record.getValueByKey("surname");
            if (surname != null) {
                patient.setSurname(surname.toString());
            }

            Object gender = record.getValueByKey("gender");
            if (gender != null) {
                int genderIndex = Integer.parseInt(gender.toString());
                if (genderIndex >= 0 && genderIndex < Gender.values().length) {
                    patient.setGender(Gender.values()[genderIndex]);
                }
            }

            patients.add(patient);
        }
    }
    return patients;
}


    public List<FluxTable> getPatientDetails(String patientId) {
        String query = "from(bucket: \"proekt\") |> range(start: -24h) |> filter(fn: (r) => r[\"patientId\"] == \"" + patientId + "\")";
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(query);
    }

    public void createPatient(Patient patient) {
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        Point point = Point.measurement("patient")
                .addTag("patientId", patient.getId())
                .addField("name", patient.getName())
                .addField("surname", patient.getSurname())
                .addField("age", patient.getAge())
                .addField("embg", patient.getEmbg())
                .addField("gender", patient.getGender().ordinal())
                .time(Instant.now(), WritePrecision.NS);
        writeApi.writePoint("proekt", "FINKI", point);
        runPythonScriptForPatient(patient);
    }
    private void runPythonScriptForPatient(Patient patient) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "C:\\Users\\PC\\IdeaProjects\\Personalized-system-for-monitoring-vital-parameters\\simulator2.py", patient.getId(), String.valueOf(patient.getAge()));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
