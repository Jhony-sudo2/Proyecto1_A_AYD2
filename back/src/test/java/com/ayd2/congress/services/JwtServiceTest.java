package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.services.Auth.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  private static final Long USER_ID = 99L;
  private static final Long ROL_ID = 1L;
  private static final Long ORG_ID = 10L;

  @InjectMocks
  private JwtService service;

  @Test
  void generateToken_thenParse_shouldContainExpectedClaims_AAA() {
    // ========== ARRANGE ==========
    
    RolEntity rol = new RolEntity();
    rol.setId(ROL_ID);
    rol.setName("ADMIN");

    OrganizationEntity org = new OrganizationEntity();
    org.setId(ORG_ID);

    UserEntity user = new UserEntity();
    user.setId(USER_ID);
    user.setEmail("jhony.fuentes19@gmail.com");
    user.setRol(rol);
    user.setOrganization(org);

    // ========== ACT ==========
    String token = service.generateToken(user);
    Claims claims = service.parseAndValidate(token);

    // ========== ASSERT ==========
    assertAll(
        () -> assertNotNull(token),
        () -> assertFalse(token.isBlank()),

        // subject = userId en string
        () -> assertEquals(String.valueOf(USER_ID), claims.getSubject()),

        // claims
        () -> assertEquals("jhony.fuentes19@gmail.com", claims.get("email", String.class)),
        () -> assertEquals("ADMIN", claims.get("rol", String.class)),
        () -> assertEquals(ROL_ID, ((Number) claims.get("rolId")).longValue()),
        () -> assertEquals(ORG_ID, ((Number) claims.get("orgId")).longValue()),

        // tiempos
        () -> assertNotNull(claims.getIssuedAt()),
        () -> assertNotNull(claims.getExpiration()),
        () -> assertTrue(claims.getExpiration().after(claims.getIssuedAt()))
    );
  }

  @Test
  void parseAndValidate_shouldThrow_whenTokenIsTampered_AAA() {
    // ========== ARRANGE ==========

    RolEntity rol = new RolEntity();
    rol.setId(ROL_ID);
    rol.setName("ADMIN");

    OrganizationEntity org = new OrganizationEntity();
    org.setId(ORG_ID);

    UserEntity user = new UserEntity();
    user.setId(USER_ID);
    user.setEmail("jhony.fuentes19@gmail.com");
    user.setRol(rol);
    user.setOrganization(org);

    String token = service.generateToken(user);
    String tampered = token.substring(0, token.length() - 1) + "x";

    // ========== ACT + ASSERT ==========
    assertThrows(JwtException.class, () -> service.parseAndValidate(tampered));
  }

  @Test
  void parseAndValidate_shouldThrow_whenExpired_AAA() throws Exception {
    // ========== ARRANGE ==========

    RolEntity rol = new RolEntity();
    rol.setId(ROL_ID);
    rol.setName("ADMIN");

    OrganizationEntity org = new OrganizationEntity();
    org.setId(ORG_ID);

    UserEntity user = new UserEntity();
    user.setId(USER_ID);
    user.setEmail("jhony.fuentes19@gmail.com");
    user.setRol(rol);
    user.setOrganization(org);

    String token = service.generateToken(user);

    // esperamos que expire
    Thread.sleep(1200);

    // ========== ACT + ASSERT ==========
    assertThrows(ExpiredJwtException.class, () -> service.parseAndValidate(token));
  }
}
