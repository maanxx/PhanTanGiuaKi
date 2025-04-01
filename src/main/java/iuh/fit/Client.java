package iuh.fit;

import iuh.fit.entity.Doctor;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("LAPTOP-TJ7ERCUH", 9090);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream((socket.getInputStream()));)
        {
            // gui yeu cau tim doctor by id
            out.writeUTF("tim doctor by id");
            out.writeUTF("DR.014");
            out.flush();
            Doctor doctor = (Doctor) in.readObject();
            System.out.println(doctor);
        }

    }
}
