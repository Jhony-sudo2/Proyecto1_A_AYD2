package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.services.Auth.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

public class JwtServiceTest {

  private static final Long USER_ID = 99L;
  private static final Long ROL_ID = 1L;
  private static final Long ORG_ID = 10L;
  private static final String SECRET_KEY = "MI-SECRET-KEY-EXAMPLE-TEST?12354678910PRUEBAPUEASDF";
  private static final long EXPSECONDS = 1;

  private JwtService service;

  @BeforeEach
  void setUp() {
    service = new JwtService(SECRET_KEY, EXPSECONDS);
  }

  @Test
  void generateToken() {
    // ARRANGE

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

    // ACT
    String token = service.generateToken(user);
    Claims claims = service.parseAndValidate(token);

    // ASSERT
    assertAll(
        () -> assertNotNull(token),
        () -> assertFalse(token.isBlank()),

        () -> assertEquals(String.valueOf(USER_ID), claims.getSubject()),

        () -> assertEquals("jhony.fuentes19@gmail.com", claims.get("email", String.class)),
        () -> assertEquals("ADMIN", claims.get("rol", String.class)),
        () -> assertEquals(ROL_ID, ((Number) claims.get("rolId")).longValue()),
        () -> assertEquals(ORG_ID, ((Number) claims.get("orgId")).longValue()),

        () -> assertNotNull(claims.getIssuedAt()),
        () -> assertNotNull(claims.getExpiration()),
        () -> assertTrue(claims.getExpiration().after(claims.getIssuedAt())));
  }

  @Test
  void parseAndValidate_shouldThrow_whenTokenIsTampered() {
    // ARRANGE

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

    // ACT ASSERT
    assertThrows(JwtException.class, () -> service.parseAndValidate(tampered));
  }

  @Test
  void parseAndValidate_shouldThrow_whenExpired() throws Exception {
    // ARRANGE

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
    Thread.sleep(1200);

    // ACT ASSERT
    assertThrows(ExpiredJwtException.class, () -> service.parseAndValidate(token));
  }
}
