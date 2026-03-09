package com.ayd2.congress.services.attendance;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.compositePrimaryKeys.AttendanceId;
import com.ayd2.congress.dtos.attendance.AttendanceResponse;
import com.ayd2.congress.dtos.attendance.NewAttendanceRequest;
import com.ayd2.congress.exceptions.ActivityAlreadyEndendException;
import com.ayd2.congress.exceptions.ActivityFullException;
import com.ayd2.congress.exceptions.ActivityNotStartedException;
import com.ayd2.congress.exceptions.CongressNotStartedException;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.AttendanceMapper;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Attendance.AttendanceEntity;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.AttendanceType;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.Attendance.AttendanceRepository;
import com.ayd2.congress.services.Inscription.InscriptionService;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.activity.ActivityService;

@Service
public class AttendanceServiceImpl implements AttendanceService{
    private final ActivityService activityService;
    private final AttendanceRepository attendanceRepository;
    private final UserService userService;
    private final InscriptionService inscriptionService;
    private final AttendanceMapper attendanceMapper;

    @Autowired
    public AttendanceServiceImpl(ActivityService activityService, AttendanceRepository attendanceRepository, UserService userService, InscriptionService inscriptionService, AttendanceMapper attendanceMapper) {
        this.activityService = activityService;
        this.attendanceRepository = attendanceRepository;
        this.userService = userService;
        this.inscriptionService = inscriptionService;
        this.attendanceMapper = attendanceMapper;
    }
    @Override
    public void createAttendance(NewAttendanceRequest request) throws NotFoundException, DuplicatedEntityException, ActivityFullException, ActivityAlreadyEndendException, ActivityNotStartedException, CongressNotStartedException {
        ActivityEntity acitivty = activityService.getActivityById(request.getActivityId());
        UserEntity user = userService.getByIdentification(request.getUserIdentification());
        CongressEntity congress = acitivty.getCongress();
        boolean exists = attendanceRepository.existsByActivityIdAndUserIdAndType(request.getActivityId(), user.getId(), request.getType());
        if(exists) throw new DuplicatedEntityException("Attendance already exists");
        boolean isInscribed = inscriptionService.isUserEnrolledInCongress(user.getId(), congress.getId());
        if(!isInscribed) throw new NotFoundException("User is not inscribed in the congress");

        if(acitivty.getType() == ActivityType.WORKSHOP && request.getType() == AttendanceType.ATTENDANCE){
            boolean isEnrolled = isEnrolledInWorkshop(user.getId(), acitivty.getId());
            if(!isEnrolled) throw new NotFoundException("User is not enrolled in the workshop");
        }
        else if (acitivty.getType() == ActivityType.WORKSHOP && request.getType() == AttendanceType.WORKSHOPINSCRIPTION) 
            verifyCapacity(acitivty.getId());
        
        
        if(congress.getStartDate().isAfter(request.getDate())) throw new CongressNotStartedException("Congress has not started yet");
        if(acitivty.getStartDate().isAfter(request.getDate())) throw new ActivityNotStartedException("Activity has not started yet");
        if(acitivty.getEndDate().isBefore(request.getDate())) throw new ActivityAlreadyEndendException("Activity has already ended");

        AttendanceEntity attendance = new AttendanceEntity();
        AttendanceId attendanceId = new AttendanceId(user.getId(), acitivty.getId(), request.getType());
        attendance.setId(attendanceId);
        attendance.setActivity(acitivty);
        attendance.setUser(user);
        attendance.setType(request.getType());
        
        attendanceRepository.save(attendance);
    }


    @Override
    public boolean verifyCapacity(Long activityId)
            throws NotFoundException,ActivityFullException{
        ActivityEntity activity = activityService.getActivityById(activityId);
        if (activity.getType() != ActivityType.WORKSHOP) {
            throw new NotFoundException("Activity is not a workshop");
        }
        if(activity.getCapacity() == 0) throw new ActivityFullException("Activity has no capacity");
        Long currentAttendanceCount = attendanceRepository.countByActivityIdAndType(activityId, AttendanceType.WORKSHOPINSCRIPTION);
        if(currentAttendanceCount >= activity.getCapacity()) throw new ActivityFullException("Activity is full");
        return true;
    }

    @Override
    public List<AttendanceResponse> getAttendanceByUserId(Long id) throws NotFoundException {
        List<AttendanceEntity> attendances = attendanceRepository.findByUserId(id);
        if(attendances.isEmpty()) throw new NotFoundException("No attendance found for the user");
        return attendanceMapper.toResponseList(attendances);
    }

    @Override
    public List<AttendanceResponse> getAttendanceByActivityId(Long id) throws NotFoundException {
        List<AttendanceEntity> attendances = attendanceRepository.findByActivityId(id);
        if(attendances.isEmpty()) throw new NotFoundException("No attendance found for the activity");
        return attendanceMapper.toResponseList(attendances);
    }
    @Override
    public boolean isEnrolledInWorkshop(Long userId, Long activityId) throws NotFoundException {
        ActivityEntity activity = activityService.getActivityById(activityId);
        if(activity.getType() != ActivityType.WORKSHOP) throw new NotFoundException("Activity is not a workshop");
        return attendanceRepository.existsByActivityIdAndUserIdAndType(activityId, userId, AttendanceType.WORKSHOPINSCRIPTION);
    }
    
}
