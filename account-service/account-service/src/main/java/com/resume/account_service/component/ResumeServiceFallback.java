package com.resume.account_service.component;

import com.resume.account_service.DTO.ResumeRequestDTO;
import com.resume.account_service.client.ResumeServiceClient;
import org.springframework.stereotype.Component;

@Component
public class ResumeServiceFallback implements ResumeServiceClient {
    @Override
    public ResumeRequestDTO getUserInfo(String emailId) {
        return new ResumeRequestDTO();
    }
}
