package iuh.fit.util;


import iuh.fit.entity.Doctor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private Socket socket;
    private ObjectInputStream in ;
    private ObjectOutputStream out ;

    public Client() throws IOException {
        socket = new Socket(HOST, PORT);
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    // addDoctor
    public boolean addDoctor(Doctor doctor) throws IOException, ClassNotFoundException {
        out.writeObject("addDoctor");
        out.writeObject(doctor);
        out.flush();
        return (boolean) in.readObject();
    }

    public Map<String, Long> getNoOfDoctorsBySpeciality(String departmentName) throws IOException, ClassNotFoundException {
        out.writeObject("getNoOfDoctorsBySpeciality");
        out.writeObject(departmentName);
        out.flush();
        return (Map<String, Long>) in.readObject();
    }

    public List<Doctor> listDoctorsBySpeciality(String keyword) throws IOException, ClassNotFoundException {
        out.writeObject("listDoctorsBySpeciality");
        out.writeObject(keyword);
        out.flush();
        return (List<Doctor>) in.readObject();
    }

    public boolean updateDiagnosis(String patientID, String doctorID, String newDiagnosis) throws IOException, ClassNotFoundException {
        out.writeObject("updateDiagnosis");
        out.writeObject(patientID);
        out.writeObject(doctorID);
        out.writeObject(newDiagnosis);
        out.flush();
        return (boolean) in.readObject();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();

            // Them bac si (thong tin sinh vien)
            Doctor d = new Doctor("SV001", "Nguyen Phan Minh Man", "0909943237", "Cardiology", "DEP001");
            boolean result = client.addDoctor(d);
            System.out.println("Them thanh cong bac si?" + result);

            // Ví dụ: Thống kê số bác sĩ
            Map<String, Long> stats = client.getNoOfDoctorsBySpeciality("Cardiology Department");
            System.out.println("Thong ke: " + stats);

            // Ví dụ: Tìm kiếm bác sĩ
            List<Doctor> doctors = client.listDoctorsBySpeciality("Cardiology");
            System.out.println("Danh sach bac si: " + doctors);

            // Ví dụ: Cập nhật chẩn đoán
            boolean updated = client.updateDiagnosis("PT001", "SV001", "New Diagnosis");
            System.out.println("Cap nhat chan doan thanh cong? " + updated);

            client.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
