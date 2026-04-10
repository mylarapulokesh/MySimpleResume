package com.resume.account_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.antlr.v4.runtime.misc.NotNull;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenManager {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "UUID_Token")
    @NotNull
    private UUID uuid;
    @NotNull
    @Column(name="EmailId")
    private String emailId;
    @NotNull
    @Column(name="expiry")
    private LocalDateTime tokenExpiry;
}
