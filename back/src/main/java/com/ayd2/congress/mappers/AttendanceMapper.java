package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ayd2.congress.dtos.attendance.AttendanceResponse;
import com.ayd2.congress.dtos.attendance.AttendanceUserResponse;
import com.ayd2.congress.models.Attendance.AttendanceEntity;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    AttendanceResponse toResponse(AttendanceEntity entity);
    List<AttendanceResponse> toResponseList(List<AttendanceEntity> entities);

    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "activityName", source = "activity.name")
    AttendanceUserResponse toUserResponse(AttendanceEntity entity);
    List<AttendanceUserResponse> toUserResponseList(List<AttendanceEntity> entities);
}
