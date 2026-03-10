package com.ayd2.congress.services.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Auth.LoginRequest;
import com.ayd2.congress.dtos.Auth.LoginResponse;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.jwt.JwtService;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(UserService userService, JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void logOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logOut'");
    }

    @Override
    public LoginResponse authenticateAndGetToken(LoginRequest request)
            throws NotFoundException, NotAuthorizedException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            throw new NotAuthorizedException("invalid credentials");
        }

        UserEntity user = userService.getByEmail(request.getEmail());
        jwtService.updateTokenExpiration(user.getEmail());

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", jwtService.getExpSeconds());
    }

}
