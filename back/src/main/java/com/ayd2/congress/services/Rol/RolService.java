package com.ayd2.congress.services.Rol;

import java.util.List;

import com.ayd2.congress.dtos.Rol.NewRolRequest;
import com.ayd2.congress.dtos.Rol.RolResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.User.RolEntity;

public interface RolService {
    RolResponse createRol(NewRolRequest request) throws DuplicatedEntityException;
    RolResponse getRolById(Long id) throws NotFoundException;
    List<RolEntity> getAllRols();
}
