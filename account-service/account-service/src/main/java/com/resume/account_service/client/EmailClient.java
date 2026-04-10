package com.resume.account_service.client;

import com.resume.account_service.DTO.WelcomeMailDTO;
import com.resume.account_service.component.EmailClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "email-service",fallback = EmailClientFallback.class)
public interface EmailClient {
    @PostMapping("/send/welcome/email")
    String sendWelcomeMail(@RequestBody WelcomeMailDTO details);
    @PostMapping("/send/password/reset")
    String sendPasswordResetMail(@RequestParam String emailId,@RequestParam UUID uuid);
}
