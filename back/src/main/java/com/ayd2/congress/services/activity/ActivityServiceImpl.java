package com.ayd2.congress.services.activity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.NewActivityRequest;
import com.ayd2.congress.dtos.acitivty.NewProposalRequest;
import com.ayd2.congress.dtos.acitivty.ProposalResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.ActivityMapper;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Activities.ProposalEntity;
import com.ayd2.congress.models.Congress.ConferenceRoomEntity;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.ProposalState;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.Activity.ActivityRepository;
import com.ayd2.congress.repositories.Attendance.ProposalRepository;
import com.ayd2.congress.services.Congress.CongressService;
import com.ayd2.congress.services.Inscription.InscriptionService;
import com.ayd2.congress.services.Location.LocationService;
import com.ayd2.congress.services.User.UserService;

@Service
public class ActivityServiceImpl implements ActivityService {
    private final LocationService locationService;
    private final ProposalRepository proposalRepository;
    private final ActivityRepository activityRepository;
    private final CongressService congressService;
    private final UserService userService;
    private final InscriptionService inscriptionService;
    private final ActivityMapper activityMapper;

    @Autowired
    private ActivityServiceImpl(LocationService locationService, ProposalRepository proposalRepository,
            ActivityRepository activityRepository, CongressService congressService, UserService userService,
            InscriptionService inscriptionService, ActivityMapper activityMapper) {
        this.locationService = locationService;
        this.proposalRepository = proposalRepository;
        this.activityRepository = activityRepository;
        this.congressService = congressService;
        this.userService = userService;
        this.activityMapper = activityMapper;
        this.inscriptionService = inscriptionService;
    }

    @Override
    public ActivityResponse createActivity(NewActivityRequest request)
            throws NotFoundException, DuplicatedEntityException, InvalidDateRangeException {
        ProposalEntity proposal = getProposalById(request.getProposalId());
        ConferenceRoomEntity room = locationService.getRoomById(request.getRoomId());
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();
        if (proposal.getState() != ProposalState.APPROVED) {
            throw new IllegalStateException("Only approved proposals can be scheduled as activities");
        }
        if (activityRepository.existsByRoomIdAndTimeRange(room.getId(), startDate, endDate)) {
            throw new DuplicatedEntityException("The room is already booked for the given time range");
        }
        if (activityRepository.existsByProposalId(proposal.getId())) {
            throw new DuplicatedEntityException("The proposal is already associated with an activity");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date must be after start date");
        }
        CongressEntity congress = proposal.getCongress();
        if (startDate.isBefore(congress.getStartDate()) || endDate.isAfter(congress.getEndDate())) {
            throw new IllegalArgumentException("Activity must be scheduled within the congress dates");
        }
        
        ActivityEntity activity = new ActivityEntity();
        activity.setProposal(proposal);
        activity.setRoom(room);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activityRepository.save(activity);
        return activityMapper.toActivityResponse(activity);
    }

    @Override
    public List<ActivityResponse> getActivitiesByCongressId(Long congressId) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getActivitiesByCongressId'");
    }

    @Override
    public List<ActivityResponse> getActivitiesByTypeAndCongressId(ActivityType type, Long congressId)
            throws NotFoundException {
        List<ActivityEntity> activities = activityRepository.findByTypeAndCongressId(type, congressId);
        return activityMapper.toActivityResponseList(activities);
    }

    @Override
    public ProposalResponse createProposal(NewProposalRequest request)
            throws NotFoundException, DuplicatedEntityException {
        CongressEntity congress = congressService.getById(request.getCongressId());
        UserEntity user = userService.getById(request.getUserId());
        boolean isEnrolled = inscriptionService.isUserEnrolledInCongress(request.getUserId(), request.getCongressId());

        if (!isEnrolled)
            throw new NotFoundException("User is not enrolled in the congress");

        boolean exists = proposalRepository.existsByUserIdAndCongressIdAndState(request.getUserId(),
                request.getCongressId(), ProposalState.PENDING);
        if (exists)
            throw new DuplicatedEntityException("User has already a pending proposal for this congress");

        ProposalEntity proposal = new ProposalEntity();
        proposal.setUser(user);
        proposal.setName(request.getName());
        proposal.setDescription(request.getDescription());
        proposal.setCongress(congress);
        proposalRepository.save(proposal);
        return activityMapper.toProposalResponse(proposal);
    }

    @Override
    public List<ProposalResponse> getProposalsByCongressId(Long congressId) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProposalsByCongressId'");
    }

    @Override
    public List<ProposalResponse> getProposalsByStateAndCongressId(ProposalState state, Long congressId)
            throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProposalsByStateAndCongressId'");
    }

    @Override
    public List<ProposalResponse> getProposalByUserId(Long userId) throws NotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProposalByUserId'");
    }

    @Override
    public ProposalEntity getProposalById(Long id) throws NotFoundException {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proposal not found with id: " + id));
    }

}
