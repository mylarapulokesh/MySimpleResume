package com.resume.resume.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PersonalDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String summary;
    private String gmail;
    @ManyToOne
    @JoinColumn(name = "emailId", referencedColumnName = "emailId") // foreign key column
    private Resume resume;
}
