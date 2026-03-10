package com.ayd2.congress.dtos.reports;

import lombok.Value;

@Value
public class InscriptionReport {
    private String identification;
    private String name;
    private String organizationName;
    private String email;
    private String phone;
    private String attendeeRolName;
}
