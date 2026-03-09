package com.ayd2.congress.services.activity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.compositePrimaryKeys.SpeakerId;
import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.NewActivityGuest;
import com.ayd2.congress.dtos.acitivty.NewActivityRequest;
import com.ayd2.congress.dtos.acitivty.NewProposalRequest;
import com.ayd2.congress.dtos.acitivty.ProposalResponse;
import com.ayd2.congress.dtos.acitivty.UpdateActivity;
import com.ayd2.congress.dtos.acitivty.UpdateProposal;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.ActivityMapper;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Activities.ProposalEntity;
import com.ayd2.congress.models.Activities.SpeakerEntity;
import com.ayd2.congress.models.Congress.ConferenceRoomEntity;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.ProposalState;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.Activity.ActivityRepository;
import com.ayd2.congress.repositories.Activity.SpeakerRepository;
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
    private final SpeakerRepository speakerRepository;

    @Autowired
    private ActivityServiceImpl(LocationService locationService, ProposalRepository proposalRepository,
            ActivityRepository activityRepository, CongressService congressService, UserService userService,
            InscriptionService inscriptionService, ActivityMapper activityMapper, SpeakerRepository speakerRepository) {
        this.locationService = locationService;
        this.proposalRepository = proposalRepository;
        this.activityRepository = activityRepository;
        this.congressService = congressService;
        this.userService = userService;
        this.activityMapper = activityMapper;
        this.inscriptionService = inscriptionService;
        this.speakerRepository = speakerRepository;
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
        if (activityRepository.existsOverlap(room.getId(), startDate, endDate)) {
            throw new DuplicatedEntityException("The room is already booked for the given time range");
        }
        if (proposal.isUsed()) {
            throw new DuplicatedEntityException("The proposal is already associated with an activity");
        }
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date must be after start date");
        }
        CongressEntity congress = proposal.getCongress();
        if (startDate.isBefore(congress.getStartDate()) || endDate.isAfter(congress.getEndDate())) {
            throw new IllegalArgumentException("Activity must be scheduled within the congress dates");
        }

        ActivityEntity activity = activityMapper.toEntity(request);
        activity.setRoom(room);
        activity.setDescription(proposal.getDescription());
        activity.setType(proposal.getType());
        activity.setCongress(congress);
        activityRepository.save(activity);
        addSpeaker(activity, proposal.getUser());
        proposal.setUsed(true);
        proposalRepository.save(proposal);
        return activityMapper.toActivityResponse(activity);
    }

    @Override
    public ActivityResponse createActivityWithGuest(NewActivityGuest request)
            throws NotFoundException, DuplicatedEntityException, InvalidDateRangeException {
        ArrayList<UserEntity> users = verifyUsers(request.getUsers());
        ConferenceRoomEntity room = locationService.getRoomById(request.getRoomId());
        LocalDateTime endDate = request.getEndDate();
        LocalDateTime startDate = request.getStartDate();

        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date must be after start date");
        }
        if (activityRepository.existsOverlap(room.getId(), startDate, endDate)) {
            throw new DuplicatedEntityException("The room is already booked for the given time range");
        }

        CongressEntity congress = congressService.getById(request.getCongressId());
        if (startDate.isBefore(congress.getStartDate()) || endDate.isAfter(congress.getEndDate())) {
            throw new InvalidDateRangeException("Activity must be scheduled within the congress dates");
        }
        verifySpeakerConflict(users, startDate, endDate);
        ActivityEntity activityEntity = activityMapper.toEntity(request);
        activityEntity.setRoom(room);
        activityEntity.setCongress(congress);
        activityRepository.save(activityEntity);
        addSpeakers(activityEntity, users);
        return activityMapper.toActivityResponse(activityEntity);
    }

    private ArrayList<UserEntity> verifyUsers(Long[] user) throws NotFoundException {
        ArrayList<UserEntity> users = new ArrayList<>();
        for (Long long1 : user) {
            UserEntity tmpUser = userService.getById(long1);
            users.add(tmpUser);
        }
        return users;
    }

    private void addSpeakers(ActivityEntity activityEntity, ArrayList<UserEntity> users)
            throws DuplicatedEntityException {
        for (UserEntity userId : users) {
            addSpeaker(activityEntity, userId);
        }
    }

    private void verifySpeakerConflict(ArrayList<UserEntity> users,LocalDateTime startDate,LocalDateTime endDate) throws DuplicatedEntityException {
        for (UserEntity userEntity : users) {
            if (speakerRepository.existsSpeakerConflict(userEntity.getId(), startDate, endDate)) {
                throw new DuplicatedEntityException("El usuario: " + userEntity.getName() + " esta ocupado en el horario establecido");
            }
        }
    }

    @Override
    public List<ActivityResponse> getActivitiesByCongressId(Long congressId) throws NotFoundException {
        List<ActivityEntity> activities = activityRepository.findByCongressId(congressId);
        return activityMapper.toActivityResponseList(activities);
    }

    private void addSpeaker(ActivityEntity activity, UserEntity user) throws DuplicatedEntityException {
        SpeakerEntity entity = new SpeakerEntity();
        SpeakerId id = new SpeakerId(activity.getId(), user.getId());
        boolean exists = speakerRepository.existsByUserIdAndActivityId(user.getId(), activity.getId());
        if (exists) {
            throw new DuplicatedEntityException("El usuario ya esta asignado a esta actividad");
        }
        entity.setActivity(activity);
        entity.setUser(user);
        entity.setId(id);
        speakerRepository.save(entity);
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

        ProposalEntity proposal = activityMapper.toProposalEntity(request);
        proposal.setUser(user);
        proposal.setCongress(congress);
        proposalRepository.save(proposal);
        return activityMapper.toProposalResponse(proposal);
    }

    @Override
    public List<ProposalResponse> getProposalsByCongressId(Long congressId) throws NotFoundException {
        List<ProposalEntity> proposals = proposalRepository.findByCongressId(congressId);
        return activityMapper.toProposalResponseList(proposals);
    }

    @Override
    public List<ProposalResponse> getProposalsByStateAndCongressId(ProposalState state, Long congressId)
            throws NotFoundException {
        List<ProposalEntity> proposals = proposalRepository.findByCongressId(congressId);
        return activityMapper.toProposalResponseList(proposals.stream()
                .filter(p -> p.getState() == state)
                .toList());
    }

    @Override
    public List<ProposalResponse> getProposalByUserId(Long userId) throws NotFoundException {
        List<ProposalEntity> proposals = proposalRepository.findByUserId(userId);
        return activityMapper.toProposalResponseList(proposals);
    }

    @Override
    public ProposalEntity getProposalById(Long id) throws NotFoundException {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Proposal not found with id: " + id));
    }

    @Override
    public ActivityEntity getActivityById(Long id) throws NotFoundException {
        return activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found with id: " + id));
    }

    @Override
    public ProposalResponse updateProposal(Long id, UpdateProposal state)
            throws NotFoundException, DuplicatedEntityException {

        ProposalEntity proposal = getProposalById(id);

        if (proposal.getState() == ProposalState.REJECTED) {
            throw new NotFoundException("Proposal was rejected");
        }
        proposal.setState(state.getState());

        if (state.getState() == ProposalState.APPROVED) {
            inscriptionService.enroll(proposal.getUser().getId(), getRoleIdByProposalType(proposal.getType()),
                    proposal.getCongress().getId(), true);
        }
        proposalRepository.save(proposal);
        return activityMapper.toProposalResponse(proposal);
    }

    private Long getRoleIdByProposalType(ActivityType type) {
        switch (type) {
            case CONFERENCE:
                return 3L;
            case WORKSHOP:
                return 4L;
            default:
                throw new IllegalArgumentException("Unsupported proposal type");
        }
    }

    @Override
    public ProposalResponse getProposalResponseById(Long id) throws NotFoundException {
        return activityMapper.toProposalResponse(getProposalById(id));
    }

    @Override
    public void deleteAcivity(Long activityId) throws NotFoundException {
        ActivityEntity entity = getActivityById(activityId);
        activityRepository.delete(entity);
    }

    @Override
    public ActivityResponse updateActivity(Long id, UpdateActivity request)
            throws NotFoundException, DuplicatedEntityException, InvalidDateRangeException {
        ActivityEntity activityToUpdate = getActivityById(id);
        ConferenceRoomEntity room = locationService.getRoomById(request.getRoomId());
        LocalDateTime endDate = request.getEndDate();
        LocalDateTime startDate = request.getStartDate();
        List<UserEntity> speakers = speakerRepository.findAllByActivityId(activityToUpdate.getId()).stream().map(SpeakerEntity::getUser).toList();
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date must be after start date");
        }
        CongressEntity congress = activityToUpdate.getCongress();
        if (startDate.isBefore(congress.getStartDate()) || endDate.isAfter(congress.getEndDate())) {
            throw new IllegalArgumentException("Activity must be scheduled within the congress dates");
        }

        boolean existsOverLap = activityRepository.existsOverlapExcludingId(request.getRoomId(), request.getStartDate(),
                request.getEndDate(), id);
        if (existsOverLap) {
            throw new DuplicatedEntityException("The room is already booked for the given time range");
        }
        verifySpeakerConflict((ArrayList<UserEntity>)speakers, startDate, endDate);
        activityToUpdate.setCapacity(request.getCapacity());
        activityToUpdate.setRoom(room);
        activityToUpdate.setStartDate(request.getStartDate());
        activityToUpdate.setEndDate(request.getEndDate());
        activityToUpdate.setName(request.getName());
        activityRepository.save(activityToUpdate);
        return activityMapper.toActivityResponse(activityToUpdate);
    }

}
