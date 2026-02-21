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

    @Autowired
    public AuthServiceImpl(UserService userService,PasswordEncoder encoder){
        this.encoder = encoder;
        this.userService = userService;
    }

    @Override
    public LoginResponse Login(LoginRequest request) throws NotFoundException, NotAuthorizedException {
        UserEntity user = userService.getByEmail(request.getEmail());
        if (encoder.matches(request.getPassword(), user.getPassword())) 
            return new LoginResponse(user.getId(), "EXAMPLE");
        throw new NotAuthorizedException("invalid credentials");
    }

    @Override
    public void logOut() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logOut'");
    }
    
}
