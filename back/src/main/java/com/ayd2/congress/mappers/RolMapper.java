package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.models.User.RolEntity;

@Mapper(componentModel = "spring")
public interface RolMapper {
    RolResponse toResponseRol(RolEntity entity);
    List<RolResponse> toListResponseRol(List<RolEntity> entities);
}
