package iuh.fit.dao;

import iuh.fit.entity.Doctor;
import iuh.fit.util.AppUtils;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.types.Node;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



public class DoctorDao implements DoctorDaoImpl{

    // find Doctor by id
    public Doctor findDoctorById(String doctorId){
        String query = "MATCH (d:Doctor {doctor_id: $doctorId}) RETURN d";

        try (Session session = AppUtils.getSession()){
            return session.executeRead(tx -> {
                Result result = tx.run(query, Map.of("doctorId", doctorId));
                if (result.hasNext()){
                    Record record = result.next();
                    Node node = record.get("d").asNode();
                    return AppUtils.toDoctor(node);
                }
                return null;
            });
        }
    }

    // add a new doctor
    public boolean addDoctor(Doctor doctor){
        String query = "CREATE (d: Doctor {doctor_id: $doctor_id, name: $name, phone: $phone, speciality: $speciality})";
        try (Session session = AppUtils.getSession()) {
            return session.executeWrite(tx -> {
                ResultSummary summary = tx.run(query, AppUtils.toMap(doctor)).consume();
                return summary.counters().nodesCreated() > 0;
            });
        }
    }

    // Thống kê số bác sỹ theo từng chuyên khoa (speciality) của một khoa (department)
    // nào đó khi biết tên khoa
    public Map<String, Long> getNoOfDoctorsBySpeciality(String departmentName){
        String query = "MATCH (d: Department{name: $departmentName})<-[:BELONG_TO]-(doc: Doctor)\n" +
                "RETURN doc.speciality as speciality, COUNT(doc) as noOfDoctors";
        try (Session session = AppUtils.getSession()){
            return session.executeRead(tx -> {
                Result result = tx.run(query, Map.of("departmentName", departmentName));

                return result.stream().collect(Collectors.toMap(
                    record -> record.get("speciality").asString(),
                        record -> record.get("noOfDoctors").asLong()
                ));
            });
        }
    }

    // Dùng full-text search, tìm kiếm các bác sỹ theo chuyên khoa.
    public List<Doctor> listDoctorsBySpeciality(String keywords) {
        try (var session = AppUtils.getSession()) {
            return session.executeRead(tx -> {
                String query = "CALL db.index.fulltext.queryNodes('txt_index_speciality', $keywords) YIELD node, score RETURN node as d";
                Result result = tx.run(query, Map.of("keywords", keywords));

                if(!result.hasNext())
                    return null;

                return result.stream()
                        .map(record -> record.get("d").asNode())
                        .map(node -> AppUtils.toDoctor(node))
                        .collect(Collectors.toList());
            });
        }
    }

    //Cập nhật lại chẩn đoán (diagnosis) của một lượt điều trị khi biết mã số bác sỹ và mã
    //số bệnh nhân. Lưu ý, chỉ được phép cập nhật khi lượt điều trị này vẫn còn đang điều
    //trị (tức ngày kết thúc điều trị là null)
    public boolean updateDiagnosis(String patientId, String doctorId, String diagnosis){
        String query = "MATCH (p:Patient {patient_id: $patientId})-[r:BE_TREATED]->(d:Doctor {doctor_id: $doctorId})\n" +
                "WHERE r.end_date IS NULL\n" +
                "SET r.diagnosis = $diagnosis";
        try (Session session = AppUtils.getSession())  {
            return session.executeWrite(tx -> {
                ResultSummary resultSummary = tx.run(query, Map.of(
                        "patientId", patientId,
                        "doctorId", doctorId,
                        "diagnosis", diagnosis)).consume();
                // Kiem tra so thuoc tinh duoc cap nhat thay vi so node tao
                return resultSummary.counters().nodesCreated() > 0;
            });
        }
    }
}
