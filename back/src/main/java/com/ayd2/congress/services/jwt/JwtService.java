package com.ayd2.congress.services.jwt;

import com.ayd2.congress.models.User.UserEntity;

public interface JwtService {
    String generateToken(UserEntity user);
    
    String getUsername(String token);
    
    boolean isValid(String token);
    
    void updateTokenExpiration(String username);
    
    boolean isTokenExpired(String username);
    long getExpSeconds();
}
