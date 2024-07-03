package finki.veb.personalizedsystemformonitoringvitalparameters.web;

import finki.veb.personalizedsystemformonitoringvitalparameters.model.Patient;
import finki.veb.personalizedsystemformonitoringvitalparameters.model.VitalRecord;
import finki.veb.personalizedsystemformonitoringvitalparameters.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class PatientController {
    @Autowired
    private PatientService patientService;


    @GetMapping("/patients")
    public String getAllPatients(Model model) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return "allpatients";
    }

    @GetMapping("/patients/{patientId}")
    public String getPatientDetails(@PathVariable String patientId, Model model) {
        List<VitalRecord> vitalRecords = patientService.getPatientDetails(patientId);
        model.addAttribute("patientId", patientId);
        model.addAttribute("vitalRecords", vitalRecords);
        return "details";
    }
}
