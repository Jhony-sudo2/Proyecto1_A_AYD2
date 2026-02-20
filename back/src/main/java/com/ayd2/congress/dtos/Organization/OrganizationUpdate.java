package com.ayd2.congress.dtos.Organization;

import lombok.Value;

@Value
public class OrganizationUpdate {
    private String name;
    private String image;
    private boolean canCreateCongress;

}
