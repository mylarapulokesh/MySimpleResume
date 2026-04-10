package com.resume.resume.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class EducationDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String instituteName;
    private String courseName;
    private String courseStartYear;
    private String courseEndYear;
    private String educationType;
    @ManyToOne
    @JoinColumn(name = "emailId", referencedColumnName = "emailId") // foreign key column
    private Resume resume;
}
