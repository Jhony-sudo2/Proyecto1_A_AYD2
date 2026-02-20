package com.ayd2.congress.services.Organization;

import java.util.List;

import com.ayd2.congress.dtos.Organization.NewOrganizationRequest;
import com.ayd2.congress.dtos.Organization.OrganizationResponse;
import com.ayd2.congress.dtos.Organization.OrganizationUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;

public class OrganizationServiceImpl implements OrganizationService{

    @Override
    public OrganizationResponse create(NewOrganizationRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public OrganizationResponse getById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public List<OrganizationResponse> getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public OrganizationResponse update(OrganizationUpdate request, Long id)
            throws NotFoundException, DuplicatedEntityException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    
    
}
