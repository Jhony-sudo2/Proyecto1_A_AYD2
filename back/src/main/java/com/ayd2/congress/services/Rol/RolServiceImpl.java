package com.ayd2.congress.services.Rol;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Rol.NewRolRequest;
import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.repositories.RolRepository;

@Service
public class RolServiceImpl implements RolService{
    private final RolRepository repository;
    @Autowired
    public RolServiceImpl(RolRepository repository){
        this.repository = repository;
    }

    @Override
    public RolResponse createRol(NewRolRequest request) throws DuplicatedEntityException {
        if(repository.existByName(request.getName()))
            throw new DuplicatedEntityException("");
        RolEntity newEntity = request.createEntity();
        newEntity = repository.save(newEntity);
        return new RolResponse(newEntity);
    }

    @Override
    public RolEntity getRolById(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Rol with id "+id+" not found"));
    }

    @Override
    public List<RolEntity> getAllRols() {
        return repository.findAll();   
    }
    
}
