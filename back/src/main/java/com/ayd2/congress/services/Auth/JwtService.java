package com.ayd2.congress.services.Auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ayd2.congress.models.User.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
private final Key key;
  private final long expSeconds;

  public JwtService(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.expiration-seconds}") long expSeconds
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expSeconds = expSeconds;
  }

  public long getExpSeconds() {
    return expSeconds;
  }

  public String generateToken(UserEntity user) {
    Instant now = Instant.now();

    // Subject = userId (puede ser email si prefieres)
    String subject = String.valueOf(user.getId());

    return Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(expSeconds)))
        .addClaims(Map.of(
            "email", user.getEmail(),
            "rolId", user.getRol().getId(),
            "orgId", user.getOrganization().getId(),
            "rol", user.getRol().getName()
        ))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseAndValidate(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
