package com.resume.account_service.component;

import com.resume.account_service.repository.TokenManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class TokenCleanupScheduler {
    @Autowired
    private TokenManagerRepository tokenManagerRepo;

    @Scheduled(fixedRate = 600000) // every 10mins
    @Transactional
    public void cleanExpiredTokens() {
        tokenManagerRepo.deleteExpiredTokens(LocalDateTime.now());
        System.out.println("Expired tokens cleaned up");
    }
}
