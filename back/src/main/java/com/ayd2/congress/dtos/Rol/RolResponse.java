package com.ayd2.congress.dtos.Rol;

import com.ayd2.congress.models.User.RolEntity;

import lombok.Value;

@Value
public class RolResponse {
    private Long id;
    private String name;
    public RolResponse(RolEntity entity){
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
