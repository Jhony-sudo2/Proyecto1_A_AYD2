package com.ayd2.congress.services.Rol;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Rol.NewRolRequest;
import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.RolMapper;
import com.ayd2.congress.models.User.RolEntity;
import com.ayd2.congress.repositories.RolRepository;

@Service
public class RolServiceImpl implements RolService{
    private final RolRepository repository;
    private final RolMapper mapper;
    @Autowired
    public RolServiceImpl(RolRepository repository,RolMapper rolMapper){
        this.repository = repository;
        this.mapper = rolMapper;
    }

    @Override
    public RolResponse createRol(NewRolRequest request) throws DuplicatedEntityException {
        if(repository.existsByName(request.getName()))
            throw new DuplicatedEntityException("");
        RolEntity newEntity = request.createEntity();
        newEntity = repository.save(newEntity);
        return mapper.toResponseRol(newEntity);
    }

    @Override
    public RolEntity getRolById(Long id) throws NotFoundException {
        return repository.findById(id).orElseThrow(()-> new NotFoundException("Rol with id "+id+" not found"));
    }
    
    @Override
    public RolResponse getRolResponseById(Long id) throws NotFoundException{
        return mapper.toResponseRol(getRolById(id));
    }

    @Override
    public List<RolResponse> getALLResponses(){
        return mapper.toListResponseRol(getAllRols());
    }

    @Override
    public List<RolEntity> getAllRols() {
        return repository.findAll();   
    }
    
}
