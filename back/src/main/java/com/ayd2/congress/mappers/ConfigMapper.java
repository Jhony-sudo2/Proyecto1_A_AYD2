package com.ayd2.congress.mappers;

import org.mapstruct.Mapper;

import com.ayd2.congress.dtos.SysConfig.SysConfigResponse;
import com.ayd2.congress.models.SystemConfigEntity;

@Mapper(componentModel = "spring")
public interface ConfigMapper {
    SysConfigResponse toResponse(SystemConfigEntity entity);
}
