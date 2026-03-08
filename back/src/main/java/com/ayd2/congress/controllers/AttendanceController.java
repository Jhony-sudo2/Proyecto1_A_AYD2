package com.ayd2.congress.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.attendance.AttendanceResponse;
import com.ayd2.congress.dtos.attendance.NewAttendanceRequest;
import com.ayd2.congress.exceptions.ActivityAlreadyEndendException;
import com.ayd2.congress.exceptions.ActivityFullException;
import com.ayd2.congress.exceptions.ActivityNotStartedException;
import com.ayd2.congress.exceptions.CongressNotStartedException;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.services.attendance.AttendanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("atteendances")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService){
        this.attendanceService = attendanceService;
    }

     @PostMapping
    public ResponseEntity<Void> createAttendance(
            @Valid @RequestBody NewAttendanceRequest request)
            throws NotFoundException, DuplicatedEntityException, ActivityFullException,
                   ActivityAlreadyEndendException, CongressNotStartedException, ActivityNotStartedException {

        attendanceService.createAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByUserId(@PathVariable Long userId)
            throws NotFoundException {

        List<AttendanceResponse> responses = attendanceService.getAttendanceByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/activities/{activityId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByActivityId(@PathVariable Long activityId)
            throws NotFoundException {

        List<AttendanceResponse> responses = attendanceService.getAttendanceByActivityId(activityId);
        return ResponseEntity.ok(responses);
    }


}
