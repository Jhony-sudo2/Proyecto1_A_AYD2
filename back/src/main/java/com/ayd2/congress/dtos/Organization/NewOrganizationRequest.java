package com.ayd2.congress.dtos.Organization;

import lombok.Value;

@Value
public class NewOrganizationRequest {
    private String name;
    private String image;
}
