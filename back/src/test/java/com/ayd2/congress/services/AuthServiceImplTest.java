package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ayd2.congress.dtos.Auth.LoginRequest;
import com.ayd2.congress.dtos.Auth.LoginResponse;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.services.Auth.AuthServiceImpl;
import com.ayd2.congress.services.User.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    private final static Long USER_ID = 1L;
    private final static String USER_EMAIL = "jhony.fuentes19@gmail.com";
    private final static String USER_PASSWORD = "example12345";
    private final static String JWTOKEN = "EXAMPLE";

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private AuthServiceImpl service;

    @Test
    void loginTest() throws NotFoundException, NotAuthorizedException{
        LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(USER_EMAIL);
        userEntity.setPassword(USER_PASSWORD);  
        userEntity.setId(USER_ID);      
        when(encoder.matches(USER_PASSWORD, USER_PASSWORD)).thenReturn(true);
        when(userService.getByEmail(USER_EMAIL)).thenReturn(userEntity);
        LoginResponse result = service.Login(request);

        assertAll(
            ()-> assertEquals(USER_ID, result.getUserId()),
            ()-> assertEquals(JWTOKEN, result.getToken())
        );
    }

    @Test
    void loginWhenIncorrectCredentialsTest() throws NotFoundException{
        LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);
        UserEntity entity = new UserEntity();
        entity.setId(USER_ID);
        entity.setEmail(USER_EMAIL);
        entity.setPassword(USER_PASSWORD);

        when(userService.getByEmail(USER_EMAIL)).thenReturn(entity);
        when(encoder.matches(USER_PASSWORD,USER_PASSWORD)).thenReturn(false);

        Assertions.assertThrows(NotAuthorizedException.class, 
            ()-> service.Login(request));
    }

}
