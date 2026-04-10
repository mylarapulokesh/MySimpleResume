package com.resume.account_service.service;

import com.resume.account_service.DTO.ResetPasswordDTO;
import com.resume.account_service.DTO.ResumeRequestDTO;
import com.resume.account_service.DTO.WelcomeMailDTO;
import com.resume.account_service.client.EmailClient;
import com.resume.account_service.client.ResumeServiceClient;
import com.resume.account_service.enums.UserRoles;
import com.resume.account_service.exception.ResourceNotFoundException;
import com.resume.account_service.model.DisplayTemplates;
import com.resume.account_service.model.TokenManager;
import com.resume.account_service.model.UserCreds;
import com.resume.account_service.projections.*;
import com.resume.account_service.repository.DisplayTemplatesRepository;
import com.resume.account_service.repository.TokenManagerRepository;
import com.resume.account_service.repository.UserCredsRepository;
import jakarta.ws.rs.InternalServerErrorException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountService {
    @Autowired
    private UserCredsRepository userCredsRepo;
    @Autowired
    private DisplayTemplatesRepository displayTemplatesRepo;
    @Autowired
    private TokenManagerRepository tokenManagerRepo;
    @Autowired
    private ResumeServiceClient resumeServiceClient;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailClient emailClient;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public ResponseEntity<String> saveCredentials(UserCreds userCreds){
        UserCreds existingUser = userCredsRepo.findByEmailId(userCreds.getEmailId());
        if (existingUser!=null){
            return new ResponseEntity<>("User Already Exists! Please Login", HttpStatus.CONFLICT);
        }
        UserCreds user = new UserCreds();
        user.setUserName(userCreds.getUserName());
        user.setEmailId(userCreds.getEmailId());
        user.setPassword(passwordEncoder.encode(userCreds.getPassword()));
        user.setRole(UserRoles.USER);
        userCredsRepo.save(user);
        try {
            WelcomeMailDTO userDetails = new WelcomeMailDTO();
            userDetails.setToMail(userCreds.getEmailId());
            userDetails.setUserName(userCreds.getUserName());
            emailClient.sendWelcomeMail(userDetails);
            logger.info("✅ Welcome mail sent successfully to: {}", user.getEmailId());
        }catch (Exception e){
            logger.error("❌ Welcome mail failed for: {} | Reason: {}", user.getEmailId(), e.getMessage());
        }
        return new ResponseEntity<>("User Details Saved Successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<String> login(UserCreds userCreds) {
        UserCreds user = userCredsRepo.findByEmailId(userCreds.getEmailId());
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        System.out.println(user.getPassword());
        System.out.println(passwordEncoder.encode(userCreds.getPassword()));
        if(passwordEncoder.matches(userCreds.getPassword(),user.getPassword())){
            return new ResponseEntity<>("Login Success", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Invalid Password", HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<ResumeRequestDTO> getUserInfo(String emailId) {
        ResumeRequestDTO response = resumeServiceClient.getUserInfo(emailId);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> saveShowCaseTemplates(DisplayTemplates templateNames) {
        displayTemplatesRepo.deleteAll();
        DisplayTemplates displayTemplates = new DisplayTemplates();
        displayTemplates.setTemplateNames(templateNames.getTemplateNames());
        displayTemplatesRepo.save(displayTemplates);
        return ResponseEntity.status(HttpStatus.OK).body(templateNames.getTemplateNames());
    }

    public ResponseEntity<List<String>> getShowCaseTemplates() {
        List<String> selectedTemplates = new ArrayList<>();
        List<ShowCaseTemplatesProjection> entityTemplates = displayTemplatesRepo.getAllSavedTemplates();
        for(ShowCaseTemplatesProjection s : entityTemplates){
            selectedTemplates.add(s.getTemplateNames());
        }
        return ResponseEntity.ok(selectedTemplates);
    }
    public ResponseEntity<String> sendPasswordResetMail(String emailId){
        try{
            String userStatus = validateUser(emailId).getBody();
            if(userStatus.equalsIgnoreCase("Valid User")){
                UUID uuid = UUID.randomUUID();
                tokenManagerRepo.deleteByEmailId(emailId);
                tokenManagerRepo.save(new TokenManager(null,uuid,emailId, LocalDateTime.now().plusMinutes(30)));
                emailClient.sendPasswordResetMail(emailId,uuid);
                return ResponseEntity.status(HttpStatus.OK).body("Password Reset Mail Sent Successfully");
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong", e);
        }
        return ResponseEntity.ok("");
    }
    public ResponseEntity<String> resetPassword(ResetPasswordDTO resetRequest){
        TokenManager userTokenDetails = tokenManagerRepo.findByEmailId(resetRequest.getEmailId());
        logger.info("UserDetails :{}", userTokenDetails);
        if (userTokenDetails!=null){
            UUID expectedUuid = userTokenDetails.getUuid();
            LocalDateTime tokenExpiry = userTokenDetails.getTokenExpiry();
            logger.info("token in request: {}",resetRequest.getToken());
            logger.info("token in DB: {}",expectedUuid);
            if (LocalDateTime.now().isBefore(tokenExpiry) && resetRequest.getToken().equals(expectedUuid.toString())){
                UserCreds user = userCredsRepo.findByEmailId(resetRequest.getEmailId());
                //overridding password
                user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
                //saving into DB
                userCredsRepo.save(user);
                tokenManagerRepo.deleteByEmailId(resetRequest.getEmailId());
                return ResponseEntity.status(HttpStatus.OK).body("Password Changed Successfully");
            } else if (LocalDateTime.now().isAfter(tokenExpiry)) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Token Expired.Kindly Resend Reset Request!");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reset Password Request Failed. Please Resend Reset Request!");
    }
    public ResponseEntity<String> validateUser(String emailId){
        UserCreds user = userCredsRepo.findByEmailId(emailId);
        if(user !=null){
            return ResponseEntity.status(HttpStatus.OK).body("Valid User");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No User Found");
    }

    public Boolean verifyUser(String emailId) {
        UserCreds user = userCredsRepo.findByEmailId(emailId);
        if(user !=null){
            return true;
        }
        return false;
    }
}
