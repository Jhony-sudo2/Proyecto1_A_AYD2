package com.ayd2.congress.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.NewActivityRequest;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.NotFoundException;
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

    @GetMapping("/{id}")
    public ResponseEntity<List<ActivityResponse>> getAllActivities(@PathVariable Long id) throws NotFoundException{
        List<ActivityResponse> responses = activityService.getActivitiesByCongressId(id);
        return ResponseEntity.ok(responses);
    }
    
}
