package com.ayd2.congress.services.Rol;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ayd2.congress.dtos.Rol.NewRolRequest;
import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.repositories.RolRepository;

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
    public RolResponse getRolById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRolById'");
    }

    @Override
    public List<RolEntity> getAllRols() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllRols'");
    }
    
}
