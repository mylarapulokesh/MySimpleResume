package com.resume.email_service.Client;

import com.resume.email_service.DTO.WelcomeMailDTO;
import com.resume.email_service.component.AccountClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "account-service",fallback = AccountClientFallback.class)
public interface AccountClientService {
    @PostMapping("/send/welcome/mail")
    WelcomeMailDTO sendWelcomeMail();

    @PostMapping("/send/password/reset")
    String sendResetPasswordMail(@RequestParam String emailId,@RequestParam UUID uuid);

    @GetMapping("/verify/user/existence")
    String validateUser(@RequestParam String emailId);
}
