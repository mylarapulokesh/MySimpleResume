package com.resume.account_service.repository;

import com.resume.account_service.model.UserCreds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredsRepository extends JpaRepository<UserCreds,String> {
    UserCreds findByEmailId(String emailId);
}
