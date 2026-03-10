package com.ayd2.congress.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ayd2.congress.dtos.certificate.CertificateResponse;
import com.ayd2.congress.models.Congress.CertificateEntity;

@Mapper(componentModel = "spring")
public interface CertificateMapper {
    @Mapping(target = "congressName",     source = "congress.name")
    @Mapping(target = "startDate",        source = "congress.startDate")
    @Mapping(target = "endDate",          source = "congress.endDate")
    @Mapping(target = "locationName",     source = "congress.location.name")
    @Mapping(target = "organizationName", source = "congress.organization.name")
    @Mapping(target = "name",             source = "user.name")
    @Mapping(target = "lastName",         source = "user.lastName")
    @Mapping(target = "date",             expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "assitantType",source = "rol.name")
    CertificateResponse toResponse(CertificateEntity entity);
}
