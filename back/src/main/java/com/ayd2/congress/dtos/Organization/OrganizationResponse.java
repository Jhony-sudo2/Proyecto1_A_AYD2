package com.ayd2.congress.dtos.Organization;

import com.ayd2.congress.models.Organization.OrganizationEntity;

import lombok.Value;

@Value
public class OrganizationResponse {
    private Long id;
    private String name;
    private String image;
    private boolean canCreateCongress;

    public OrganizationResponse(OrganizationEntity data){
        this.id = data.getId();
        this.name = data.getName();
        this.image = data.getImage();
        this.canCreateCongress = data.isCanCreateCongress();
    }
}
