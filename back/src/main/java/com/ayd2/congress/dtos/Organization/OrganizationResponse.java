package com.ayd2.congress.dtos.Organization;
import lombok.Value;

@Value
public class OrganizationResponse {
    private Long id;
    private String name;
    private String image;
    private boolean canCreateCongress;
}
