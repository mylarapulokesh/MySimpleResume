package com.resume.resume.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String emailId;
    private String personName;
    @ElementCollection
    private List<String> languagesKnown;
    @ElementCollection
    private List<String> skillSet;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<Projects> projects;
    @OneToMany(mappedBy="resume",cascade = CascadeType.ALL)
    private List<Internships> internships;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<EducationDetails> educationDetails;
    @OneToMany(mappedBy="resume",cascade =CascadeType.ALL)
    private List<SocialMediaUrls> socialMediaUrls;
}
