package iuh.fit.util;


import iuh.fit.dao.DoctorDAOimpl;
import iuh.fit.dao.impl.DoctorDAO;
import iuh.fit.entity.Doctor;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class Server {
    private static  final int PORT = 8080;
    private final DoctorDAOimpl doctorDAOimpl;
    private ServerSocket serverSocket;
    private Driver driver;


    public Server() {
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "minhman910"));
        this.doctorDAOimpl = new DoctorDAO(driver);
        initializeData();
    }

    private void initializeData() {
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
            if (!session.run("SHOW INDEXES WHERE name = 'doctorSpecialityIndex'").hasNext()) {
                session.run("CREATE FULLTEXT INDEX doctorSpecialityIndex FOR (n:Doctor) ON EACH [n.speciality]");
            }
            session.run("MERGE (dept:Department {id: 'DEP001', name: 'Cardiology Department'})");
            session.run("MERGE (p:Patient {id: 'PT001', name: 'Patient One'})");
            session.run("MERGE (d:Doctor {id: 'SV001', name: 'Nguyen Phan Minh Man', phone: '0909943237', speciality: 'Cardiology'})" +
                    "-[:BELONG_TO]->(:Department {id: 'DEP001'})");
            session.run("MERGE (d:Doctor {id: 'SV001'})-[r:BE_TREATED {diagnosis: 'Initial Diagnosis'}]->(p:Patient {id: 'PT001'})");
            System.out.println("Initialized database with basic data.");
        } catch (Exception e) {
            System.err.println("Failed to initialize data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
            while (true)  {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected" + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e){
            System.out.println("Error starting server");
            e.printStackTrace();
        }
    }

    public void stop() {
        try{
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server stopped");
            }
            if (driver != null) {
                driver.close();
                System.out.println("Neo4j Driver closed");
            }
        } catch (Exception e){
            System.out.println("Error stopping server" + e.getMessage());
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {

        private final Socket clientSocket;
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())
            ) {
                while (true) {
                    try {
                        String request = (String) in.readObject();
                        if (request == null) break; // close client
                        System.out.println("Received request: " + request);

                        switch (request) {
                            case "addDoctor":
                                Doctor doctor = (Doctor) in.readObject();
                                boolean addDoctor = doctorDAOimpl.addDoctor(doctor);
                                out.writeObject(addDoctor);
                                break;
                            case "getNoOfDoctorsBySpeciality":
                                String departmentName = (String) in.readObject();
                                Map<String, Long> stats = doctorDAOimpl.getNoOfDoctorsBySpeciality(departmentName);
                                out.writeObject(stats);
                                break;

                            case "listDoctorsBySpeciality":
                                String keyword = (String) in.readObject();
                                List<Doctor> doctors = doctorDAOimpl.listDoctorsBySpeciality(keyword);
                                out.writeObject(doctors);
                                break;

                            case "updateDiagnosis":
                                String patientID = (String) in.readObject();
                                String doctorID = (String) in.readObject();
                                String newDiagnosis = (String) in.readObject();
                                boolean updateResult = doctorDAOimpl.updateDiagnosis(patientID, doctorID, newDiagnosis);
                                out.writeObject(updateResult);
                                break;
                            default:
                                System.out.println("Valid request: ");
                        }
                        out.flush();
                    } catch (EOFException e) {
                        System.out.println("Client disconnected"); // client disconnected
                        break; // close
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error handling client: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
