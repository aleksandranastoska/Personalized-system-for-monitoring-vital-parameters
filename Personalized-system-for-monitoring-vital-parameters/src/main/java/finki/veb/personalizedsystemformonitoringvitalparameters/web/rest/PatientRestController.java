package finki.veb.personalizedsystemformonitoringvitalparameters.web.rest;

import com.influxdb.query.FluxTable;
import finki.veb.personalizedsystemformonitoringvitalparameters.model.Patient;
import finki.veb.personalizedsystemformonitoringvitalparameters.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientRestController {
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
    public String getAllPatients(Model model) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return "allpatients";
    }

    @GetMapping("/{patientId}")
    public List<FluxTable> getPatientDetails(@PathVariable String patientId) {
        return patientService.getPatientDetails(patientId);
    }
}
