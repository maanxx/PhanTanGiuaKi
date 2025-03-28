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
public class Patient extends Person implements Serializable {
    private String gender;
    private String dateOfBirth;
    private String address;

    public Patient(String id, String name, String phone, String gender, String dateOfBirth, String address) {
        super(id, name, phone);
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}
