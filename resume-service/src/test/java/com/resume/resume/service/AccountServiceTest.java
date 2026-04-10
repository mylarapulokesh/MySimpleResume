//package com.resume.resume.service;
//
//import com.resume.resume.Repository.UserCredsRepository;
//import com.resume.resume.model.UserCreds;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AccountServiceTest {
//
//    @Mock
//    private UserCredsRepository userCredsRepository;
//
//    @InjectMocks
//    private AccountService accountService;
//
//    private UserCreds userCreds;
//
//    @BeforeEach
//    void setup(){
//        userCreds = new UserCreds("lokesh@gmail.com","Lokesh","123456");
//    }
//
//    @Test
//    void saveCredentials() {
//        when(userCredsRepository.save(userCreds)).thenReturn(userCreds);
//        ResponseEntity<UserCreds> savedUser = accountService.saveCredentials(userCreds);
//        assertEquals(userCreds.getUserName(),savedUser.getBody().getUserName());
//        assertEquals(userCreds.getPassword(),savedUser.getBody().getPassword());
//        assertEquals(userCreds.getEmailId(),savedUser.getBody().getEmailId());
//    }
//    @Test
//    void login_userNotFound() {
//        UserCreds expectedUser = new UserCreds("loke@gmail.com","Lokesh","123456");
//
//        when(userCredsRepository.findByEmailId(expectedUser.getEmailId()))
//                .thenReturn(null);
//
//        ResponseEntity<String> loggedInUser = accountService.login(expectedUser);
//
//        assertEquals("User not found", loggedInUser.getBody());
//        assertEquals(HttpStatus.NOT_FOUND, loggedInUser.getStatusCode());
//    }
//    @Test
//    void login_success() {
//        UserCreds expectedUser = new UserCreds("lokesh@gmail.com","Lokesh","123456");
//
//        when(userCredsRepository.findByEmailId(expectedUser.getEmailId()))
//                .thenReturn(userCreds);
//
//        ResponseEntity<String> loggedInUser = accountService.login(expectedUser);
//
//        assertEquals("Login Success", loggedInUser.getBody());
//        assertEquals(HttpStatus.OK, loggedInUser.getStatusCode());
//    }
//    @Test
//    void login_invalidPassword() {
//        UserCreds expectedUser = new UserCreds("lokesh@gmail.com","Lokesh","999999");
//
//        when(userCredsRepository.findByEmailId(expectedUser.getEmailId()))
//                .thenReturn(userCreds);
//
//        ResponseEntity<String> loggedInUser = accountService.login(expectedUser);
//
//        assertEquals("Invalid Password", loggedInUser.getBody());
//        assertEquals(HttpStatus.UNAUTHORIZED, loggedInUser.getStatusCode());
//    }
//
//    @Test
//    void getUserInfo() {
//    }
//}