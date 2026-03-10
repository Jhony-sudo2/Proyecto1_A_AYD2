package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ayd2.congress.dtos.Organization.NewOrganizationRequest;
import com.ayd2.congress.dtos.Organization.OrganizationResponse;
import com.ayd2.congress.models.Organization.OrganizationEntity;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "canCreateCongress",ignore = true)
    OrganizationEntity toEntity(NewOrganizationRequest request);
    
    OrganizationResponse toResponse(OrganizationEntity entity);
    List<OrganizationResponse> toResponseList(List<OrganizationEntity> entities);
}
