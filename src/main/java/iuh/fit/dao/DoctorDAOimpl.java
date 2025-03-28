package iuh.fit.dao;

import iuh.fit.entity.Doctor;

import java.util.List;
import java.util.Map;

public interface DoctorDAOimpl {

    boolean addDoctor(Doctor doctor);

    Map<String, Long> getNoOfDoctorsBySpeciality (String departmentName);

    List<Doctor> listDoctorsBySpeciality(String keyword);

    boolean updateDiagnosis(String patientID, String doctorID, String newDiagnosis);
}
