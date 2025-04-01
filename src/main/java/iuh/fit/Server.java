package iuh.fit;

import iuh.fit.dao.DoctorDao;
import iuh.fit.dao.DoctorDaoImpl;
import iuh.fit.entity.Doctor;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(9090)) {
            System.out.println("Server is running...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress());
                System.out.println(socket.getPort());

                Thread thread = new Thread(new HandlingClient(socket));
                thread.start();
            }


        }
    }
}

class HandlingClient implements Runnable{

    private Socket socket;
    private DoctorDaoImpl doctorDao;

    public HandlingClient(Socket socket) {
        this.socket = socket;
        doctorDao = new DoctorDao();
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());) {

            String request = in.readUTF();

            // nhan yeu cau doctor by id
            if (request.equals("tim doctor by id")){
                String doctorId = in.readUTF();
                Doctor doctor = doctorDao.findDoctorById(doctorId);
                out.writeObject(doctor);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
