package com.ayd2.congress.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.Organization.NewOrganizationRequest;
import com.ayd2.congress.dtos.Organization.OrganizationResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.services.Organization.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
    
    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(@Valid @RequestBody NewOrganizationRequest request) throws DuplicatedEntityException {
        OrganizationResponse response = organizationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations() {
        List<OrganizationResponse> organizations = organizationService.getAll();
        return ResponseEntity.ok(organizations);
    }

}
