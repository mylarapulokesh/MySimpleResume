package com.resume.account_service.DTO;

import lombok.Data;

import java.util.List;

@Data
public class WelcomeMailDTO {
    private String toMail;
    private String userName;
}
