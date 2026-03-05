package com.ayd2.congress.dtos.attendance;

import com.ayd2.congress.models.Enums.AttendanceType;

import lombok.Value;

@Value
public class AttendanceResponse {
    private Long userId;
    private String userName;
    private AttendanceType type;
}
