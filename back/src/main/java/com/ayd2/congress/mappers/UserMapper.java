package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.UserRegister;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.models.User.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "rol", ignore = true),
            @Mapping(target = "organization", ignore = true),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "tokenExpiration",ignore = true),
            @Mapping(target = "recoveryCode",ignore = true),
            @Mapping(target = "codeExpiration",ignore = true),
    })
    UserEntity toEntity(NewUserRequest dto);
    @Mappings({
            @Mapping(target = "organizationName", source = "organization.name")
    })
    UserResponse toResponse(UserEntity entity);
    List<UserResponse> toResponseList(List<UserEntity> entities);
    @Mapping(target ="rol",constant = "2L")
    NewUserRequest toRequest(UserRegister register);
}
