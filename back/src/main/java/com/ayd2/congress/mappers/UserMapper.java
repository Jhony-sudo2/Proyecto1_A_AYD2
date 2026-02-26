package com.ayd2.congress.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.ayd2.congress.dtos.User.NewUserRequest;
import com.ayd2.congress.dtos.User.UserResponse;
import com.ayd2.congress.models.User.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "rol", ignore = true),
            @Mapping(target = "organization", ignore = true),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "active", constant = "true")
    })
    UserEntity toEntity(NewUserRequest dto);
    @Mappings({
            @Mapping(target = "rolId", source = "rol.id"),
            @Mapping(target = "organizationId", source = "organization.id")
    })
    UserResponse toResponse(UserEntity entity);
}
