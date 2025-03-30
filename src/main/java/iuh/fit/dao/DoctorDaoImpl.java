package iuh.fit.dao;

import iuh.fit.entity.Doctor;

import java.util.List;
import java.util.Map;

public interface DoctorDaoImpl {

    Doctor findDoctorById(String doctorId);

    boolean addDoctor(Doctor doctor);

    Map<String, Long> getNoOfDoctorsBySpeciality(String departmentName);

    List<Doctor> listDoctorsBySpeciality(String keywords);
}
