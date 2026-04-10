package com.resume.email_service.controller;

import com.resume.email_service.DTO.WelcomeMailDTO;
import com.resume.email_service.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class EmailController {
    @Autowired
    EmailService emailService;
    @PostMapping("/send/welcome/email")
    public ResponseEntity<String> sendEmail(@RequestBody WelcomeMailDTO userDetails){
        return emailService.sendEmail(userDetails);
    }
    @PostMapping("/send/password/reset")
    public ResponseEntity<String> sendPasswordResetMail(@RequestParam String emailId,@RequestParam UUID uuid) {
        return emailService.sendPasswordResetMail(emailId,uuid);
    }


}
