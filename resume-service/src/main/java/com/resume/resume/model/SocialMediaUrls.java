package com.resume.resume.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class SocialMediaUrls {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String linkedInUrl;
    private String gitHubUrl;
    private String portfolioUrl;
    @ManyToOne
    @JoinColumn(name = "emailId", referencedColumnName = "emailId") // foreign key column
    private Resume resume;
}
