package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ayd2.congress.dtos.Location.LocationResponse;
import com.ayd2.congress.dtos.Location.NewLocationRequest;
import com.ayd2.congress.dtos.Location.NewRoomRequest;
import com.ayd2.congress.dtos.Location.RoomResponse;
import com.ayd2.congress.models.Congress.ConferenceRoomEntity;
import com.ayd2.congress.models.Congress.LocationEntity;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(target = "id", ignore = true)
    LocationEntity toEntity(NewLocationRequest entity);
    LocationResponse toResponse(LocationEntity entity);
    List<LocationResponse> toResponseList(List<LocationEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    ConferenceRoomEntity toRoomEntity(NewRoomRequest request);
    RoomResponse toRoomResponse(ConferenceRoomEntity entity);
    List<RoomResponse> toRoomResponseList(List<ConferenceRoomEntity> entities);

}
