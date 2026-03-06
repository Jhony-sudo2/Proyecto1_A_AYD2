package com.ayd2.congress.services.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
    private final Key key;
    private final long expSeconds;
    private final UserRepository userRepository;

    public JwtServiceImpl(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds}") long expSeconds,
            UserRepository userRepository) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expSeconds = expSeconds;
        this.userRepository = userRepository;
    }

    @Override
    public long getExpSeconds() {
        return expSeconds;
    }

    @Override
    public String generateToken(UserEntity user) {
        Instant now = Instant.now();

        //String subject = String.valueOf(user.getId());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expSeconds)))
                .addClaims(Map.of(
                        "email", user.getEmail(),
                        "rolId", user.getRol().getId(),
                        "id", user.getId(),
                        "organizationId", user.getOrganization().getId()))
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

    @Override
    public String getUsername(String token) {
        Claims claims = parseAndValidate(token);
        return claims.getSubject();
    }

    @Override
    public boolean isValid(String token) {
        try {
            parseAndValidate(token); // valida firma + exp
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void updateTokenExpiration(String username) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        UserEntity user = userOpt.get();
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
    }

    @Override
    public boolean isTokenExpired(String username) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        UserEntity user = userOpt.get();
        return user.getTokenExpiration() == null
                || LocalDateTime.now().isAfter(user.getTokenExpiration());
    }
}
