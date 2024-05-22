package finki.veb.personalizedsystemformonitoringvitalparameters.web;

import com.influxdb.query.FluxTable;
import finki.veb.personalizedsystemformonitoringvitalparameters.model.Patient;
import finki.veb.personalizedsystemformonitoringvitalparameters.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
//    private final PatientService patientService;
//
//    public PatientController(PatientService patientService) {
//        this.patientService = patientService;
//    }
//    @GetMapping("/patients")
//    public String getPatients(Model model){
//        List<Patient> patients = patientService.getAllPatients();
//        model.addAttribute("patients", patients);
//        return "allpatients";
//    }
    @Autowired
    private PatientService patientService;

    @GetMapping
    public List<FluxTable> getAllPatients(Model model) {
        List<FluxTable> tables = patientService.getAllPatients();

        model.addAttribute("tables", tables);
        return tables;
    }

    @GetMapping("/{patientId}")
    public List<FluxTable> getPatientDetails(@PathVariable String patientId) {
        return patientService.getPatientDetails(patientId);
    }
}
