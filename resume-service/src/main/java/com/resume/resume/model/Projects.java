package com.resume.resume.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Projects {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectTitle;
    private String projectDescription;
    @ManyToOne
    @JoinColumn(name = "emailId", referencedColumnName = "emailId") // foreign key column
    private Resume resume;
}
