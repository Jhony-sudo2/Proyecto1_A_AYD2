package com.ayd2.congress.dtos.Rol;

import com.ayd2.congress.models.User.RolEntity;

import lombok.Value;

@Value
public class NewRolRequest {
    String name;
    public RolEntity createEntity(){
        RolEntity newEntity = new RolEntity();
        newEntity.setName(name);
        return newEntity; 
    }
}
