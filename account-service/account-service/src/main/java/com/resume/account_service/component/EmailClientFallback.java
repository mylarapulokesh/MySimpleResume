package com.resume.account_service.component;

import com.resume.account_service.client.EmailClient;
import com.resume.account_service.DTO.WelcomeMailDTO;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Component
public class EmailClientFallback implements EmailClient {
    private static final Logger logger = LoggerFactory.getLogger(EmailClientFallback.class);
    @Override
    public String sendWelcomeMail(WelcomeMailDTO details) {
        logger.warn("⚠️ Email service is DOWN! Fallback triggered for: {}",
                details.getToMail());
        return null;
    }

    @Override
    public String sendPasswordResetMail(String emailId, UUID uuid) {
        return "";
    }
}
