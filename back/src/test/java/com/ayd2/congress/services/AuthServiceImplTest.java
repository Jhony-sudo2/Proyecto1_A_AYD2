package com.ayd2.congress.services;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

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

    private static final String EMAIL = "juan@mail.com";
    private static final String PASSWORD = "Password123";
    private static final String TOKEN = "jwt-token";
    private static final String TOKEN_TYPE = "Bearer";
    private static final long EXPIRES_IN = 3600L;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest(EMAIL, PASSWORD);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(EMAIL);
        userEntity.setName("Juan");
    }

    @Test
    void testAuthenticateAndGetToken() throws Exception {
        // Arrange
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.getByEmail(EMAIL)).thenReturn(userEntity);
        when(jwtService.generateToken(userEntity)).thenReturn(TOKEN);
        when(jwtService.getExpSeconds()).thenReturn(EXPIRES_IN);

        // Act
        LoginResponse result = authService.authenticateAndGetToken(loginRequest);

        // Assert
        assertAll(
                () -> verify(authenticationManager).authenticate(authCaptor.capture()),
                () -> verify(jwtService).updateTokenExpiration(EMAIL),
                () -> assertEquals(EMAIL, authCaptor.getValue().getPrincipal()),
                () -> assertEquals(PASSWORD, authCaptor.getValue().getCredentials()),
                () -> assertEquals(TOKEN, result.getAccesToken()),
                () -> assertEquals(TOKEN_TYPE, result.getTokenType()),
                () -> assertEquals(EXPIRES_IN, result.getExpiresIn())
        );
    }

    @Test
    void testAuthenticateAndGetTokenWhenCredentialsAreInvalid() throws NotFoundException {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Assert
        assertThrows(NotAuthorizedException.class,
                () -> authService.authenticateAndGetToken(loginRequest));

        verify(userService, never()).getByEmail(EMAIL);
        verify(jwtService, never()).updateTokenExpiration(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testAuthenticateAndGetTokenWhenUserNotFound() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.getByEmail(EMAIL)).thenThrow(new NotFoundException("User not found"));

        // Assert
        assertThrows(NotFoundException.class,
                () -> authService.authenticateAndGetToken(loginRequest));

        verify(jwtService, never()).updateTokenExpiration(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testLogOutShouldThrowUnsupportedOperationException() {
        // Assert
        assertThrows(UnsupportedOperationException.class,
                () -> authService.logOut());
    }
}