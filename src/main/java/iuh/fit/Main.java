package iuh.fit;

import iuh.fit.dao.DoctorDAOimpl;
import iuh.fit.dao.impl.DoctorDAO;
import iuh.fit.entity.Doctor;
import org.neo4j.driver.*;

import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "minhman910"));
        DoctorDAOimpl doctorService = new DoctorDAO(driver);

        // Xóa dữ liệu cũ
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
            System.out.println("Old data cleared.");
        } catch (Exception e) {
            System.out.println("Failed to clear old data: " + e.getMessage());
        }

        // Tạo dữ liệu cơ bản và index
        try (Session session = driver.session()) {
            // Kiểm tra và tạo full-text index nếu chưa tồn tại
            Result result = session.run("SHOW INDEXES WHERE name = 'doctorSpecialityIndex'");
            if (!result.hasNext()) {
                session.run("CREATE FULLTEXT INDEX doctorSpecialityIndex " +
                        "FOR (n:Doctor) " +
                        "ON EACH [n.speciality]");
                System.out.println("Full-text index created successfully.");
            } else {
                System.out.println("Full-text index already exists.");
            }

            // Tạo Department cho addDoctor
            session.run("MERGE (dept:Department {id: 'DEP001', name: 'Cardiology Department'})");
            System.out.println("Department DEP001 created or already exists.");
        }catch (Exception e) {
            System.out.println("Setup failed: " + e.getMessage());
            e.printStackTrace(); // In chi tiết lỗi để debug
            driver.close();
            return;
        }
        // Thêm bác sĩ mới
        Doctor doctor = new Doctor("DR.001", "John Smith", "0987654321", "Cardiology", "DEP001");
        boolean added = doctorService.addDoctor(doctor);
        System.out.println("Bac si da them thanh cong? " + added);

        // Khoa cần thống kê
        String departmentName = "ENT";
        Map<String, Long> statistics = doctorService.getNoOfDoctorsBySpeciality(departmentName);

        System.out.println("So bac si khoa: " + departmentName);
        if (statistics.isEmpty()) {
            System.out.println("Khong co du lieu thong ke.");
        } else {
            statistics.forEach((speciality, count) ->
                    System.out.println(String.format("Chuyen khoa: %s - So luong: %d", speciality, count))
            );
        }

        // tim bac si theo chuyen khoa
        String keyword = "Family";
        List<Doctor> doctors = doctorService.listDoctorsBySpeciality(keyword);

        if (doctors.isEmpty()) {
            System.out.println("Khong tim thay bac si nao cho tu khoa: " + keyword);
        } else {
            doctors.forEach(System.out::println);
        }
        // Cap nhat lai chan doan
        boolean updated = doctorService.updateDiagnosis("PT001", "DR.001", "Updated Diagnosis");
        System.out.println("Cap nhat lai chan doan thanh cong? " + updated);

        driver.close();
    }

}