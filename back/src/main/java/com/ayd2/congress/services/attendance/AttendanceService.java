package com.ayd2.congress.services.attendance;

import java.util.List;

import com.ayd2.congress.dtos.attendance.AttendanceResponse;
import com.ayd2.congress.dtos.attendance.NewAttendanceRequest;
import com.ayd2.congress.exceptions.ActivityAlreadyEndendException;
import com.ayd2.congress.exceptions.ActivityFullException;
import com.ayd2.congress.exceptions.ActivityNotStartedException;
import com.ayd2.congress.exceptions.CongressNotStartedException;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;

public interface AttendanceService {
    void createAttendance(NewAttendanceRequest request) throws NotFoundException,DuplicatedEntityException,ActivityFullException,ActivityAlreadyEndendException,CongressNotStartedException,ActivityNotStartedException;
    boolean verifyCapacity(Long activityId) throws NotFoundException,ActivityFullException;
    List<AttendanceResponse> getAttendanceByUserId(Long id) throws NotFoundException;
    List<AttendanceResponse> getAttendanceByActivityId(Long id) throws NotFoundException;
    boolean isEnrolledInWorkshop(Long userId, Long activityId) throws NotFoundException;
}
