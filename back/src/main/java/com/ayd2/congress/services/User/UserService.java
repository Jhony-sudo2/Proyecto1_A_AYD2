package com.ayd2.congress.services.User;

import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.dtos.User.UserUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.UserEntity;

public interface UserService {
    UserResponse create(NewUserRequest newUserRequest) throws NotFoundException,DuplicatedEntityException;
    UserEntity getById(Long id) throws NotFoundException;
    UserEntity getByEmail(String email) throws NotFoundException;
    UserResponse update(UserUpdate userUpdate, Long id) throws NotFoundException,DuplicatedEntityException;
    UserResponse updateRol();

}
