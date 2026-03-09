package com.ayd2.congress.dtos.reports;

import lombok.Value;

@Value
public class WorkshopParticipant {
    String identification;
    String fullName;
    String email;
    String attendeeRolName;
}