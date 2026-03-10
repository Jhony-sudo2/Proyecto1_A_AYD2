package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.UserRepository;
import com.ayd2.congress.services.jwt.JwtServiceImpl;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final long EXP_SECONDS = 3600L;

    private static final Long USER_ID = 1L;
    private static final Long ROL_ID = 2L;
    private static final Long ORGANIZATION_ID = 3L;
    private static final String EMAIL = "juan@mail.com";

    @Mock
    private UserRepository userRepository;

    private JwtServiceImpl jwtService;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET, EXP_SECONDS, userRepository);

        RolEntity rolEntity = new RolEntity();
        rolEntity.setId(ROL_ID);
        rolEntity.setName("ADMIN");

        OrganizationEntity organizationEntity = new OrganizationEntity();
        organizationEntity.setId(ORGANIZATION_ID);
        organizationEntity.setName("OpenAI");

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setEmail(EMAIL);
        userEntity.setRol(rolEntity);
        userEntity.setOrganization(organizationEntity);
    }

    @Test
    void testGetExpSeconds() {
        // Act
        long result = jwtService.getExpSeconds();

        // Assert
        assertEquals(EXP_SECONDS, result);
    }

    @Test
    void testGenerateToken() {
        // Act
        String token = jwtService.generateToken(userEntity);

        // Assert
        assertTrue(token != null && !token.isBlank());
    }

    @Test
    void testParseAndValidate() {
        // Arrange
        String token = jwtService.generateToken(userEntity);

        // Act
        Claims claims = jwtService.parseAndValidate(token);

        // Assert
        assertAll(
                () -> assertEquals(EMAIL, claims.getSubject()),
                () -> assertEquals(EMAIL, claims.get("email", String.class)),
                () -> assertEquals(USER_ID.intValue(), claims.get("id", Integer.class)),
                () -> assertEquals(ROL_ID.intValue(), claims.get("rolId", Integer.class)),
                () -> assertEquals(ORGANIZATION_ID.intValue(), claims.get("organizationId", Integer.class))
        );
    }

    @Test
    void testGetUsername() {
        // Arrange
        String token = jwtService.generateToken(userEntity);

        // Act
        String result = jwtService.getUsername(token);

        // Assert
        assertEquals(EMAIL, result);
    }

    @Test
    void testIsValidWhenTokenIsCorrect() {
        // Arrange
        String token = jwtService.generateToken(userEntity);

        // Act
        boolean result = jwtService.isValid(token);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValidWhenTokenIsInvalid() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act
        boolean result = jwtService.isValid(invalidToken);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValidWhenTokenWasSignedWithAnotherSecret() {
        // Arrange
        JwtServiceImpl anotherJwtService =
                new JwtServiceImpl("abcdefghijklmnopqrstuvwxyz123456", EXP_SECONDS, userRepository);

        String token = anotherJwtService.generateToken(userEntity);

        // Act
        boolean result = jwtService.isValid(token);

        // Assert
        assertFalse(result);
    }

    @Test
    void testUpdateTokenExpiration() {
        // Arrange
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(userEntity));

        LocalDateTime before = LocalDateTime.now();

        // Act
        jwtService.updateTokenExpiration(EMAIL);

        LocalDateTime after = LocalDateTime.now();

        // Assert
        assertAll(
                () -> verify(userRepository).save(userCaptor.capture()),
                () -> assertTrue(userCaptor.getValue().getTokenExpiration().isAfter(before.plusMinutes(14))),
                () -> assertTrue(userCaptor.getValue().getTokenExpiration().isBefore(after.plusMinutes(16)))
        );
    }

    @Test
    void testUpdateTokenExpirationWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Assert
        assertThrows(RuntimeException.class,
                () -> jwtService.updateTokenExpiration(EMAIL));
    }

    @Test
    void testIsTokenExpiredWhenExpirationIsNull() {
        // Arrange
        userEntity.setTokenExpiration(null);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = jwtService.isTokenExpired(EMAIL);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTokenExpiredWhenExpirationIsPast() {
        // Arrange
        userEntity.setTokenExpiration(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = jwtService.isTokenExpired(EMAIL);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTokenExpiredWhenExpirationIsFuture() {
        // Arrange
        userEntity.setTokenExpiration(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(userEntity));

        // Act
        boolean result = jwtService.isTokenExpired(EMAIL);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsTokenExpiredWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        // Assert
        assertThrows(RuntimeException.class,
                () -> jwtService.isTokenExpired(EMAIL));
    }
}