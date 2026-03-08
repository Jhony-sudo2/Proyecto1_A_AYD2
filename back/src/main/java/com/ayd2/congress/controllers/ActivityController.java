package com.ayd2.congress.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.NewActivityRequest;
import com.ayd2.congress.dtos.acitivty.NewProposalRequest;
import com.ayd2.congress.dtos.acitivty.ProposalResponse;
import com.ayd2.congress.dtos.acitivty.UpdateActivity;
import com.ayd2.congress.dtos.acitivty.UpdateProposal;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Enums.ProposalState;
import com.ayd2.congress.services.activity.ActivityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    private final ActivityService activityService;
    
    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<ActivityResponse> createActivity(@Valid @RequestBody NewActivityRequest request) throws NotFoundException, DuplicatedEntityException, InvalidDateRangeException{
        ActivityResponse response = activityService.createActivity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponse> updateActivity(@Valid @RequestBody UpdateActivity request,@PathVariable Long id) throws NotFoundException, DuplicatedEntityException, InvalidDateRangeException{
        ActivityResponse response = activityService.updateActivity(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) throws NotFoundException, DuplicatedEntityException, InvalidDateRangeException{
        activityService.deleteAcivity(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/proposal")
    public ResponseEntity<ProposalResponse> createProposal(@Valid @RequestBody NewProposalRequest request) throws NotFoundException, DuplicatedEntityException{
        ProposalResponse response = activityService.createProposal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/proposal/{id}")
    public ResponseEntity<ProposalResponse> getProposalById(@PathVariable Long id) throws NotFoundException{
        ProposalResponse response = activityService.getProposalResponseById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/proposal/user/{userId}")
    public ResponseEntity<List<ProposalResponse>> getProposalByUserId(@PathVariable Long userId) throws NotFoundException{
        List<ProposalResponse> response = activityService.getProposalByUserId(userId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/proposal/congress/{congressId}")
    public ResponseEntity<List<ProposalResponse>> getProposalByCongressId(@PathVariable Long congressId) throws NotFoundException{
        List<ProposalResponse> responses = activityService.getProposalsByCongressId(congressId);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/proposal/congress/{congressId}/{state}")
    public ResponseEntity<List<ProposalResponse>> getProposalByCongressIdAndState(@PathVariable Long congressId,@PathVariable ProposalState state) throws NotFoundException{
        List<ProposalResponse> responses = activityService.getProposalsByStateAndCongressId(state,congressId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/proposal/{id}")
    public ResponseEntity<ProposalResponse> updateProposal(@PathVariable Long id,@RequestBody UpdateProposal request) throws NotFoundException{
        ProposalResponse response = activityService.updateProposal(id, request);
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ActivityResponse>> getAllActivities(@PathVariable Long id) throws NotFoundException{
        List<ActivityResponse> responses = activityService.getActivitiesByCongressId(id);
        return ResponseEntity.ok(responses);
    }
    
}
