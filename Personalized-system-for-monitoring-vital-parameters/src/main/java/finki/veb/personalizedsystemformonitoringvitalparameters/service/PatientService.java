package finki.veb.personalizedsystemformonitoringvitalparameters.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import finki.veb.personalizedsystemformonitoringvitalparameters.model.Patient;
import finki.veb.personalizedsystemformonitoringvitalparameters.model.VitalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.*;

@Service
public class PatientService {
    @Autowired
    private InfluxDBClient influxDBClient;

    @GetMapping("/patients")
    public List<Patient> getAllPatients() {
        String flux = "from(bucket:\"proekt\") |> range(start: -1h)";
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux);

        Map<String, Patient> patientMap = new HashMap<>();

        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                String patientId = fluxRecord.getMeasurement();
                Patient patient = patientMap.getOrDefault(patientId, new Patient());
                patient.setId(patientId);
                patientMap.put(patientId, patient);
            }
        }

        influxDBClient.close();
        return new ArrayList<>(patientMap.values());
    }

    public List<VitalRecord> getPatientDetails(String patientId) {
        String query = "from(bucket: \"proekt\") |> range(start: -24h) |> filter(fn: (r) => r[\"_measurement\"] == \"" + patientId + "\")";
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(query);

        List<VitalRecord> vitalRecords = new ArrayList<>();

        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                VitalRecord record = new VitalRecord();
                record.setPatientId(patientId);
                record.setTime(fluxRecord.getTime().toString());

                switch (fluxRecord.getField()) {
                    case "temperature":
                        record.setTemperature((Double) fluxRecord.getValue());
                        break;
                    case "pulse":
                        record.setPulse((Integer) fluxRecord.getValue());
                        break;
                    case "respiration_rate":
                        record.setRespirationRate((Integer) fluxRecord.getValue());
                        break;
                    case "systolic":
                        record.setSystolic((Integer) fluxRecord.getValue());
                        break;
                    case "diastolic":
                        record.setDiastolic((Integer) fluxRecord.getValue());
                        break;
                    case "ecg_string":
                        record.setEcgString((String) fluxRecord.getValue());
                        break;
                }

                vitalRecords.add(record);
            }
        }

        influxDBClient.close();
        return vitalRecords;
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
