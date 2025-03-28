package iuh.fit.dao.impl;

import iuh.fit.dao.DoctorDAOimpl;
import iuh.fit.entity.Doctor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;


public class DoctorDAO implements DoctorDAOimpl {
    private final Driver driver;

    public DoctorDAO(Driver driver) {
        this.driver = driver;
    }

    // add doctor vao relationship
    public boolean addDoctor(Doctor doctor) {
        try (Session session = driver.session()) {
            String query = "MATCH (d:Department {department_id: $DepartmentID})" +
                    "CREATE (doc:Doctor {doctor_id: $ID, Name: $Name, Phone: $Phone, Speciality: $Speciality})"
                    + "CREATE (doc)-[:BELONG_TO]->(d)";
            session.run(query, parameters(
                    "DepartmentID", doctor.getDepartmentId(),
                    "ID", doctor.getId(),
                    "Name", doctor.getName(),
                    "Phone", doctor.getPhone(),
                    "Speciality", doctor.getSpeciality()
                    ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // thong ke số bác sỹ theo từng chuyên khoa (speciality) của một khoa (department)
    //nào đó khi biết tên khoa.
    /*
    MATCH (doc:Doctor)-[r:BELONG_TO]->(d:Department{name: "Radiology"}) RETURN doc.Speciality AS speciality, COUNT(doc) as slBacSi
    */

    public Map<String, Long> getNoOfDoctorsBySpeciality(String departmentName) {
        String query = "MATCH (doc:Doctor)-[r:BELONG_TO]->(d:Department{name: $departmentName}) RETURN doc.Speciality AS speciality, COUNT(doc) as slBacSi";

        Map<String, Long> result = new HashMap<>();

        try (Session session = driver.session()) {
            Result resultQuery = session.run(query, parameters("departmentName", departmentName));

            while (resultQuery.hasNext()) {
                Record record = resultQuery.next();
                result.put(record.get("speciality").asString(), record.get("slBacSi").asLong());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // c. Tìm kiếm bác sĩ theo chuyên khoa bằng full-text search

    public List<Doctor> listDoctorsBySpeciality(String keyword) {
        String query = " CALL db.index.fulltext.queryNodes('doctorSpecialityIndex', $keyword) YIELD node " +
                "RETURN node.ID AS doctor_id, node.Name AS name, node.Phone AS phone, node.Speciality AS speciality";


        List<Doctor> doctors = new ArrayList<>();
        try (Session session = driver.session()) {
            session.run(query, Map.of("keyword", "*" + keyword + "*"))
                    .stream()
                    .forEach(record -> doctors.add(new Doctor(
                            record.get("doctor_id").asString(),
                            record.get("name").asString(),
                            record.get("phone").asString(),
                            record.get("speciality").asString()
                    )));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return doctors;
    }

    // d. Cập nhật chẩn đoán của một lượt điều trị nếu bệnh nhân vẫn đang điều trị
    public boolean updateDiagnosis(String patientID, String doctorID, String newDiagnosis) {
        String query = """
            MATCH (d:Doctor {doctor_id: $DoctorID})<-[:BE_TREATED]-(p:Patient {patient_id: $PatientID})
            MATCH (d)<-[r:BE_TREATED]-(p)
            WHERE r.endDate IS NULL
            SET r.diagnosis = $Diagnosis
            RETURN count(r) AS updatedCount
        """;

        try (Session session = driver.session()) {
            int updated = session.run(query, Map.of(
                    "DoctorID", doctorID,
                    "PatientID", patientID,
                    "Diagnosis", newDiagnosis
            )).single().get("updatedCount").asInt();

            return updated > 0;
        }
    }
}
