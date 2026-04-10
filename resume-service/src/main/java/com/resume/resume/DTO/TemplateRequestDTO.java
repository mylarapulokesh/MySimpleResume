package com.resume.resume.DTO;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequestDTO {
    private String templateName;
    private String puppeteerTemplatePath;
    private String displayName;
    private String category;
    private String description;
    private List<String> tags;
    private String previewImage;
}
