package com.resume.account_service.model;

import com.resume.account_service.enums.UserRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserCreds {
    @Id
    @Column(unique = true)
    private String emailId;
    private String userName;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRoles role;
    private Boolean isActive;
}
