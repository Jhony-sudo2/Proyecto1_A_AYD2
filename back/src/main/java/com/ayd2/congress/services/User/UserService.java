package com.ayd2.congress.services.User;

import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.UserResponse;

public interface UserService {
    UserResponse create(NewUserRequest newUserRequest);
}
