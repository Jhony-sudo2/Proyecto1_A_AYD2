package com.ayd2.congress.services.Auth;

import com.ayd2.congress.dtos.Auth.LoginRequest;
import com.ayd2.congress.dtos.Auth.LoginResponse;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;

public interface AuthService {
    LoginResponse Login(LoginRequest request) throws NotFoundException,NotAuthorizedException;
    void logOut();
    
}
