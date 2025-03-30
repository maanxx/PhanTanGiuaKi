package iuh.fit.dao;


import iuh.fit.entity.Department;
import iuh.fit.entity.Doctor;
import org.junit.jupiter.api.*;

import javax.print.Doc;
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
        Doctor doctor = new Doctor("22679171", "Nguyen Phan Minh Man", "0909943237", "Radiology Oncology");
        boolean result = doctorDao.addDoctor(doctor);
        assertTrue(result, "Doctor was not added successfully");
    }

    @Test
    void getNoOfDoctorsBySpecialityTest() {
       String departmentName = "Family Medicine";
        Map<String, Long> result = doctorDao.getNoOfDoctorsBySpeciality(departmentName);

        assertNotNull(result, "result is null");

        assertEquals(1, result.getOrDefault("Family Medicine", 0L), "So bac si chuyen khoa Family Medicine dung!");
        assertEquals(2, result.getOrDefault("Family Medicine", 0L), "So bac si chuyen khoa Family Medicine sai!");
    }

    @Test
    void listDoctorsBySpeciality() {
        String keywords = "Family Medicine";
        List<Doctor> doctorList = doctorDao.listDoctorsBySpeciality(keywords);

        assertNotNull(doctorList, "doctorList is null");

        for (Doctor doctor : doctorList) {
            assertEquals("Family Medicine", doctor.getSpeciality(), "Doctor has incorrect speciality");
        }
    }
    @AfterAll
    void tearDown() {
        doctorDao = null;
    }

}
