package com.ayd2.congress.services.Organization;

import java.util.List;

import com.ayd2.congress.dtos.Organization.NewOrganizationRequest;
import com.ayd2.congress.dtos.Organization.OrganizationResponse;
import com.ayd2.congress.dtos.Organization.OrganizationUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Organization.OrganizationEntity;

public interface OrganizationService {
    OrganizationResponse create(NewOrganizationRequest request) throws DuplicatedEntityException;
    OrganizationEntity getById(Long id) throws NotFoundException;
    List<OrganizationResponse> getAll();
    OrganizationResponse update(OrganizationUpdate request,Long id) throws NotFoundException, DuplicatedEntityException;
}
