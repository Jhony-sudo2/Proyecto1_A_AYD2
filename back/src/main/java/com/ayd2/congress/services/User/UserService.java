package com.ayd2.congress.services.User;

import java.io.IOException;
import java.util.List;

import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.dtos.User.ConfirmCode;
import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.RecoverPassword;
import com.ayd2.congress.dtos.User.UpdatePassword;
import com.ayd2.congress.dtos.User.UserRegister;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.dtos.User.UserUpdate;
import com.ayd2.congress.exceptions.CodeAlreadyExpiredException;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotAuthorizedException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.UserEntity;

public interface UserService {
    UserResponse create(NewUserRequest newUserRequest) throws NotFoundException,DuplicatedEntityException,IOException;
    UserResponse registerUserNormal(UserRegister newUserRequest) throws NotFoundException,DuplicatedEntityException,IOException;
    UserEntity getById(Long id) throws NotFoundException;
    UserEntity getByEmail(String email) throws NotFoundException;
    UserResponse update(UserUpdate userUpdate, Long id) throws NotFoundException,DuplicatedEntityException,IOException;
    UserResponse updateRol(Long rolId,Long userId) throws NotFoundException;
    UserResponse getByIdResponse(Long id) throws NotFoundException;
    void updatePassword(UpdatePassword request,Long id)throws NotFoundException,NotAuthorizedException;
    UserResponse changeState(Long id) throws NotFoundException;
    UserEntity getByIdentification(String identification) throws NotFoundException;
    List<UserResponse> getAllUsers();
    void recoverPassword(RecoverPassword request) throws NotFoundException;
    void confirmCode(ConfirmCode request) throws NotFoundException,CodeAlreadyExpiredException;
    List<RolResponse> getAllRols();
}
