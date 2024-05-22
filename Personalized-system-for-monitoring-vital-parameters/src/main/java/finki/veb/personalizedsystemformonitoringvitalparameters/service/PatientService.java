package finki.veb.personalizedsystemformonitoringvitalparameters.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import finki.veb.personalizedsystemformonitoringvitalparameters.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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
    @GetMapping("/patients")
    public List<FluxTable> getAllPatients() {
//        String query = "from(bucket: \"proekt\") |> range(start: -24h) |> filter(fn: (r) => r[\"_measurement\"] == \"patient1\")";
//        QueryApi queryApi = influxDBClient.getQueryApi();
//        return queryApi.query(query);
        String flux = "from(bucket:\"proekt\") |> range(start: -1h)";

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
            }
        }

        influxDBClient.close();
        return tables;
    }

    public List<FluxTable> getPatientDetails(String patientId) {
        String query = "from(bucket: \"proekt\") |> range(start: -24h) |> filter(fn: (r) => r[\"patientId\"] == \"" + patientId + "\")";
        QueryApi queryApi = influxDBClient.getQueryApi();
        return queryApi.query(query);
    }

}
