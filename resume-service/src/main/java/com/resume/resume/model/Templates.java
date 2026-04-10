package com.resume.resume.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Templates {
    @Id
    private String templateName;
    private String puppeteerTemplatePath;
    private String displayName;
    private String category;
    private String description;
    @ElementCollection
    private List<String> tags;
    private String previewImage;
}
