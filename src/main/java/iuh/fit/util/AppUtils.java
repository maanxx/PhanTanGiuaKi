package iuh.fit.util;

import iuh.fit.entity.Doctor;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

import java.util.Map;

public class AppUtils {
    public static final String DB_NAME = "man22679171";

    public static Driver getDriver(){
        String userName = "neo4j";
        String passWord = "minhman910";
        String url = "neo4j://localhost:7687";
        return GraphDatabase.driver(url, AuthTokens.basic(userName, passWord));
    }

    public static Session getSession(){
        return getDriver().session(SessionConfig.forDatabase(DB_NAME));
    }
    // ToString: Doctor("D123", "Dr. John Doe", "123-456-789", "Cardiology")
    public static Doctor toDoctor(Node node) {
        return new Doctor(
                node.get("doctor_id").asString(),
                node.get("name").asString(),
                node.get("phone").asString(),
                node.get("speciality").asString()
        );
    }

    // ToString: {doctor_id=D123, name=Dr. John Doe, phone=123-456-789, speciality=Cardiology}
    public static Map<String, Object> toMap(Doctor doctor){
        return Map.of(
                "doctor_id", doctor.getId(),
                "name", doctor.getName(),
                "phone", doctor.getPhone(),
                "speciality", doctor.getSpeciality()
        );
    }
}
