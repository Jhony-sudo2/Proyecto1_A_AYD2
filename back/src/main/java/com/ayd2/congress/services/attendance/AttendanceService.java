package com.ayd2.congress.services.attendance;

import java.util.List;

import com.ayd2.congress.dtos.attendance.AttendanceResponse;
import com.ayd2.congress.dtos.attendance.NewAttendanceRequest;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;

public interface AttendanceService {
    void createAttendance(NewAttendanceRequest request) throws NotFoundException,DuplicatedEntityException;
    void createWorkshopInscription(NewAttendanceRequest request) throws NotFoundException,DuplicatedEntityException;
    List<AttendanceResponse> getAttendanceByUserId(Long id) throws NotFoundException;
    List<AttendanceResponse> getAttendanceByActivityId(Long id) throws NotFoundException;
}
