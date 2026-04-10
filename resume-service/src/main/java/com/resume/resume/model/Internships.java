package com.resume.resume.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Internships {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String internshipTitle;
    private String internshipDescription;
    @ManyToOne
    @JoinColumn(name = "emailId", referencedColumnName = "emailId") // foreign key column
    private Resume resume;
}
