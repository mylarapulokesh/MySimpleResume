package com.resume.resume.client;

import com.resume.resume.component.AccountClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service",fallback = AccountClientFallback.class)
public interface AccountClient {
    @PostMapping ("/verify/user")
    Boolean verifyUser(@RequestParam("emailId") String emailId);
}
