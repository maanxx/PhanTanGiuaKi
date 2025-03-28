package iuh.fit.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Treatment implements Serializable {
    private LocalDate startDate;
    private LocalDate endDate;
    private String diagnosis;
    private Doctor doctor;
    private Patient patient;
}
