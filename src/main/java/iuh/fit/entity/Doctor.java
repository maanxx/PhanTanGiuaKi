package iuh.fit.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Doctor extends Person implements Serializable {
    private String speciality;
    private String departmentId;

    public Doctor(String id, String name, String phone, String speciality, String departmentId) {
        super(id, name, phone);
        this.speciality = speciality;
        this.departmentId = departmentId;
    }

    public Doctor(String id, String name, String phone, String speciality) {
        super(id, name, phone);
        this.speciality = speciality;
    }
    @Override
    public String toString() {
        return String.format("Doctor{id='%s', name='%s', phone='%s', speciality='%s'}",
                id, name, phone, speciality);
    }
}
