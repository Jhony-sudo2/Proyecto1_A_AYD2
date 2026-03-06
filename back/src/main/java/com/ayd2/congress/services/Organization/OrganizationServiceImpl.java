package com.ayd2.congress.services.Organization;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Organization.NewOrganizationRequest;
import com.ayd2.congress.dtos.Organization.OrganizationResponse;
import com.ayd2.congress.dtos.Organization.OrganizationUpdate;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.OrganizationMapper;
import com.ayd2.congress.models.Organization.OrganizationEntity;
import com.ayd2.congress.repositories.OrganizationRepository;

@Service
public class OrganizationServiceImpl implements OrganizationService{
    private final OrganizationRepository repository;
    private final OrganizationMapper mapper;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository repository,OrganizationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public OrganizationResponse create(NewOrganizationRequest request) throws DuplicatedEntityException {
        if(repository.existsByName(request.getName())){
            throw new DuplicatedEntityException("Organization with name "+request.getName()+" already exists");
        }
        OrganizationEntity entity = mapper.toEntity(request);
        entity = repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    public OrganizationEntity getById(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Organization with id "+id+" not found"));
    }

    @Override
    public List<OrganizationResponse> getAll() {
        List<OrganizationEntity> list = repository.findAll();
        return mapper.toResponseList(list);
    }

    @Override
    public OrganizationResponse update(OrganizationUpdate request, Long id)
            throws NotFoundException, DuplicatedEntityException {
        OrganizationEntity organizationUpdate = getById(id);
        boolean exist = repository.existsByNameAndIdNot(request.getName(), id);
        if(exist){
            throw new DuplicatedEntityException("Organization with name "+request.getName()+" already exists");
        }
        organizationUpdate.setName(request.getName());
        organizationUpdate.setImage(request.getImage());
        organizationUpdate.setCanCreateCongress(request.isCanCreateCongress());
        repository.save(organizationUpdate);
        return mapper.toResponse(organizationUpdate);
    }

    @Override
    public OrganizationResponse getByIdResponse(Long id) throws NotFoundException {
        return mapper.toResponse(getById(id));
    }

    
    
}
