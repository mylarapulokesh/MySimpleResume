package com.resume.account_service.client;

import com.resume.account_service.DTO.ResumeRequestDTO;
import com.resume.account_service.component.ResumeServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "resume-service",fallback = ResumeServiceFallback.class)
public interface ResumeServiceClient {

    @GetMapping("/internal/get/information/{emailId}")
    ResumeRequestDTO getUserInfo(@PathVariable("emailId") String emailId);

}
