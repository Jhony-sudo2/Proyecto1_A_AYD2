package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import com.ayd2.congress.services.jwt.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    private final static String USER_EMAIL = "jhony.fuentes19@gmail.com";
    private final static String USER_PASSWORD = "example12345";
    private static final String HASHED_PASSWORD = "$2a$10$HASHED...";
    private final static String JWTOKEN = "EXAMPLE";
    private static final long EXPIRES_IN = 900L;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthServiceImpl service;

    @Test
    void loginTest() throws NotFoundException, NotAuthorizedException {
        // ARRANGE 
        LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(USER_EMAIL);
        userEntity.setPassword(HASHED_PASSWORD);

        when(userService.getByEmail(USER_EMAIL)).thenReturn(userEntity);
        when(encoder.matches(USER_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(userEntity)).thenReturn(JWTOKEN);
        when(jwtService.getExpSeconds()).thenReturn(EXPIRES_IN);

        // ACT 
        LoginResponse result = service.authenticateAndGetToken(request);

        // ASSERT 
        assertAll(
                () -> assertEquals(JWTOKEN, result.getAccesToken()), 
                () -> assertEquals("Bearer", result.getTokenType()),
                () -> assertEquals(EXPIRES_IN, result.getExpiresIn()));

        verify(userService).getByEmail(USER_EMAIL);
        verify(encoder).matches(USER_PASSWORD, HASHED_PASSWORD);
        verify(jwtService).generateToken(userEntity);
        verify(jwtService).getExpSeconds();
        verifyNoMoreInteractions(userService, encoder, jwtService);
    }

    @Test
    void loginWhenInvalidCredentials() throws NotFoundException {
        // ARRANGE 
        LoginRequest request = new LoginRequest(USER_EMAIL, USER_PASSWORD);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(USER_EMAIL);
        userEntity.setPassword(HASHED_PASSWORD);

        when(userService.getByEmail(USER_EMAIL)).thenReturn(userEntity);
        when(encoder.matches(USER_PASSWORD, HASHED_PASSWORD)).thenReturn(false);

        //  ACT ASSERT
        assertThrows(NotAuthorizedException.class, () -> service.authenticateAndGetToken(request));

        verify(userService).getByEmail(USER_EMAIL);
        verify(encoder).matches(USER_PASSWORD, HASHED_PASSWORD);
        verify(jwtService, never()).generateToken(any());
        verify(jwtService, never()).getExpSeconds();
    }

}
