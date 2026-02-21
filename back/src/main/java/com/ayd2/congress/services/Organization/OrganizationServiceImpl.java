package com.ayd2.congress.services.Organization;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Organization.NewOrganizationRequest;
import com.ayd2.congress.dtos.Organization.OrganizationResponse;
import com.ayd2.congress.dtos.Organization.OrganizationUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.repositories.OrganizationRepository;

@Service
public class OrganizationServiceImpl implements OrganizationService{
    private final OrganizationRepository repository;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository repository) {
        this.repository = repository;
    }

    @Override
    public OrganizationResponse create(NewOrganizationRequest request) throws DuplicatedEntityException {
        if(repository.existByName(request.getName())){
            throw new DuplicatedEntityException("Organization with name "+request.getName()+" already exists");
        }
        OrganizationEntity entity = request.createEntity();
        entity = repository.save(entity);
        return new OrganizationResponse(entity);
    }

    @Override
    public OrganizationEntity getById(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Organization with id "+id+" not found"));
    }

    @Override
    public List<OrganizationResponse> getAll() {
        return repository.findAll().stream().map(OrganizationResponse::new).toList();
    }

    @Override
    public OrganizationResponse update(OrganizationUpdate request, Long id)
            throws NotFoundException, DuplicatedEntityException {
        OrganizationEntity organizationUpdate = getById(id);
        boolean exist = repository.existByNameAndIdNot(request.getName(), id);
        if(exist){
            throw new DuplicatedEntityException("Organization with name "+request.getName()+" already exists");
        }
        organizationUpdate.setName(request.getName());
        organizationUpdate.setImage(request.getImage());
        organizationUpdate.setCanCreateCongress(request.isCanCreateCongress());
        return new OrganizationResponse(repository.save(organizationUpdate));
    }

    
    
}
