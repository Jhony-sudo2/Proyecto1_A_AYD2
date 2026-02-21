package com.ayd2.congress.dtos.Organization;

import com.ayd2.congress.models.Organization.OrganizationEntity;

import lombok.Value;

@Value
public class NewOrganizationRequest {
    private String name;
    private String image;

    public OrganizationEntity createEntity(){
        OrganizationEntity entity = new OrganizationEntity();
        entity.setName(name);
        entity.setImage(image);
        return entity;
    }
}
