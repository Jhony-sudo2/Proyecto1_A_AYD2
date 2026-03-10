package com.ayd2.congress.dtos.reports;

import java.util.List;

import lombok.Value;

@Value
public class WorkshopReport {
    String workshopName;
    Long capacity;
    Long total;
    Long available;
    List<WorkshopParticipant> participants;
}
