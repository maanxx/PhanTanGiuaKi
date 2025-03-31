package iuh.fit.dao;


import iuh.fit.entity.Doctor;
import iuh.fit.entity.Treatment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DoctorDaoImplTest {
    private DoctorDaoImpl doctorDao;

    @BeforeAll
    void setUp() {
        doctorDao = new DoctorDao();
    }

    @Test
    void findDoctorByIdTest() {
        Doctor doctor = doctorDao.findDoctorById("DR.014");
        assertEquals("George Hall", doctor.getName());
        assertEquals("0345.678.901", doctor.getPhone());
        assertEquals("Pathology Of Laboratory", doctor.getSpeciality());
    }
    @Test
    void findDoctorNullByIdTest(){
        Doctor doctor = doctorDao.findDoctorById("DR.114");
        assertNull(doctor);
    }

    @Test
    void addDoctorTest() {
        Doctor doctor = new Doctor("DR.000", "Nguyen Phan Minh Man", "0909943237", "Radiology Oncology");
        boolean result = doctorDao.addDoctor(doctor);
        assertTrue(result, "Doctor was not added successfully");
    }

    @Test
    void getNoOfDoctorsBySpecialityTest() {
        /// departmentName: la cai khoa
        ///  specialityName: la cai chuyen khoa
       String departmentName = "General Surgery";
        Map<String, Long> result = doctorDao.getNoOfDoctorsBySpeciality(departmentName);

        assertNotNull(result, "result is null");

        assertEquals(1, result.getOrDefault("General Surgery", 1L), "So bac si chuyen khoa General Surgery dung!");
        assertEquals(1, result.getOrDefault("Surgery", 1L), "So bac si chuyen khoa General Surgery dung!");
        assertEquals(1, result.getOrDefault("Bariatric Surgery", 1L), "So bac si chuyen khoa General Surgery dung!");

    }

    @Test
    void listDoctorsBySpeciality() {
        String keywords = "Cardiology";
        List<Doctor> doctorList = doctorDao.listDoctorsBySpeciality(keywords);

        assertNotNull(doctorList, "doctorList is null");

        assertTrue(doctorList.size() > 0, "doctorList is empty");
    }

    @Test
    void updateDiagnosisTest() {
        String patientId = "PT005";
        String doctorId = "DR.014";
        String diagnosis = "Pathology";
        boolean treatment = doctorDao.updateDiagnosis(patientId, doctorId, diagnosis);

        assertFalse(treatment, "Diagnosis was not updated successfully");
    }
    @AfterAll
    void tearDown() {
        doctorDao = null;
    }

}
