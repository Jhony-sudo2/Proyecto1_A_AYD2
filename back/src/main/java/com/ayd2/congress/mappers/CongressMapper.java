package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ayd2.congress.dtos.Congress.CongressResponse;
import com.ayd2.congress.dtos.Congress.NewCongressRequest;
import com.ayd2.congress.models.Congress.CongressEntity;

@Mapper(componentModel = "spring")
public interface CongressMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization",ignore = true)
    @Mapping(target = "location",ignore = true)
    @Mapping(target = "imageUrl",ignore = true)
    CongressEntity toEntity(NewCongressRequest request);

    @Mapping(target = "organizationName",ignore = true)
    @Mapping(target = "locationName",ignore = true)
    CongressResponse toResponse(CongressEntity entity);

    List<CongressResponse> toCongressResponseList(List<CongressEntity> entities);
}
