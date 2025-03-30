package iuh.fit.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Department implements Serializable {
    private String id;
    private String name;
    private String location;
}
