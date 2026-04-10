package com.resume.email_service.component;

import com.resume.email_service.Client.AccountClientService;
import com.resume.email_service.DTO.WelcomeMailDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountClientFallback implements AccountClientService {
    @Override
    public WelcomeMailDTO sendWelcomeMail() {
        return new WelcomeMailDTO();
    }

    @Override
    public String sendResetPasswordMail(String emailId, UUID uuid) {
        return "";
    }

    @Override
    public String validateUser(String emailId) {
        return "";
    }
}
