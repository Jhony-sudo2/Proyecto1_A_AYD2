package com.ayd2.congress.services.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Auth.LoginRequest;
import com.ayd2.congress.dtos.Auth.LoginResponse;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.services.User.UserService;

@Service
public class AuthServiceImpl implements AuthService{
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Autowired
    public AuthServiceImpl(UserService userService,PasswordEncoder encoder,JwtService jwtService){
        this.encoder = encoder;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse Login(LoginRequest request) throws NotFoundException, NotAuthorizedException {
        UserEntity user = userService.getByEmail(request.getEmail());
        if (!encoder.matches(request.getPassword(), user.getPassword())) 
           throw new NotAuthorizedException("invalid credentials");
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", jwtService.getExpSeconds());
    }

    @Override
    public void logOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logOut'");
    }
    
}
