package com.resume.account_service.controller;

//import com.resume.account_service.DTO.ResumeRequestDTO;
import com.resume.account_service.DTO.ResetPasswordDTO;
import com.resume.account_service.DTO.ResumeRequestDTO;
import com.resume.account_service.repository.UserCredsRepository;
import com.resume.account_service.model.DisplayTemplates;
import com.resume.account_service.model.UserCreds;
import com.resume.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class AccountController {

    @Autowired
    private UserCredsRepository userCredsRepo;
    @Autowired
    private AccountService accountService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCreds userCredsDetails) {

       return accountService.login(userCredsDetails);
    }

    @PostMapping("/create/account")
    public ResponseEntity<String> createAccount(@RequestBody UserCreds userCredsDetails){
        return accountService.saveCredentials(userCredsDetails);
    }

    @GetMapping("/get/information/{emailId}")
    public ResponseEntity<ResumeRequestDTO> getUserInfo(@PathVariable String emailId){
        return accountService.getUserInfo(emailId);
    }

    @PostMapping("/save/showcase/templates")
    public ResponseEntity<?> saveShowCaseTemplates(@RequestBody DisplayTemplates templateNames){
        return accountService.saveShowCaseTemplates(templateNames);
    }

    @GetMapping("/get/showcase/templates")
    public ResponseEntity<List<String>> selectedTemplates(){
        return accountService.getShowCaseTemplates();
    }

    @PostMapping("/send/password/reset")
    public ResponseEntity<String> sendResetPasswordMail(@RequestParam String emailId){
        return accountService.sendPasswordResetMail(emailId);
    }

    @PostMapping("/reset/password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetRequest){
        return accountService.resetPassword(resetRequest);
    }

    @GetMapping("/verify/user/existence")
    public ResponseEntity<String> validateUser(@RequestParam String emailId){
        return accountService.validateUser(emailId);
    }
    @PostMapping("/verify/user")
    public Boolean verifyUser(@RequestParam String emailId){
        return accountService.verifyUser(emailId);
    }
}