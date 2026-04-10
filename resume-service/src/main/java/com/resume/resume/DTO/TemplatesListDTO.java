package com.resume.resume.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplatesListDTO {
    List<TemplateRequestDTO> allTemplates;
}