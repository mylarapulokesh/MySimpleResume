package com.resume.account_service.repository;

import com.resume.account_service.model.TokenManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface TokenManagerRepository extends JpaRepository<TokenManager,Long> {
    TokenManager findByEmailId(String emailId);
    @Transactional
    void deleteByEmailId(String emailId);
    @Transactional
    @Modifying
    @Query("DELETE FROM TokenManager t WHERE t.tokenExpiry < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
