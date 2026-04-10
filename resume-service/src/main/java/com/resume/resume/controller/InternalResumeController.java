package com.resume.resume.controller;

import com.resume.resume.DTO.ResumeRequestDTO;
import com.resume.resume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalResumeController {
    @Autowired
    ResumeService resumeService;
    @GetMapping("/get/information/{emailId}")
    public ResponseEntity<ResumeRequestDTO> getUserInfo(@PathVariable String emailId) {
        return resumeService.getUserInfo(emailId);
    }
}
