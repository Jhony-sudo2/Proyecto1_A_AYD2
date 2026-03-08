package com.ayd2.congress.services.activity;

import java.util.List;

import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.NewActivityRequest;
import com.ayd2.congress.dtos.acitivty.NewProposalRequest;
import com.ayd2.congress.dtos.acitivty.ProposalResponse;
import com.ayd2.congress.dtos.acitivty.UpdateActivity;
import com.ayd2.congress.dtos.acitivty.UpdateProposal;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Activities.ProposalEntity;
import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.ProposalState;

public interface ActivityService {
    ActivityResponse createActivity(NewActivityRequest request) throws NotFoundException,DuplicatedEntityException,InvalidDateRangeException;
    List<ActivityResponse> getActivitiesByCongressId(Long congressId) throws NotFoundException;
    List<ActivityResponse> getActivitiesByTypeAndCongressId(ActivityType type, Long congressId) throws NotFoundException;
    ProposalResponse createProposal(NewProposalRequest request) throws NotFoundException,DuplicatedEntityException;
    ProposalEntity getProposalById(Long id) throws NotFoundException;
    List<ProposalResponse> getProposalsByCongressId(Long congressId) throws NotFoundException;
    List<ProposalResponse> getProposalsByStateAndCongressId(ProposalState state, Long congressId) throws NotFoundException;
    List<ProposalResponse> getProposalByUserId(Long userId) throws NotFoundException;
    ActivityEntity getActivityById(Long id) throws NotFoundException;
    ProposalResponse updateProposal(Long id,UpdateProposal state) throws NotFoundException;
    ProposalResponse getProposalResponseById(Long id) throws NotFoundException;
    void deleteAcivity(Long activityId) throws NotFoundException;
    ActivityResponse updateActivity(Long id,UpdateActivity updateActivity) throws NotFoundException,DuplicatedEntityException,InvalidDateRangeException;
    
}
