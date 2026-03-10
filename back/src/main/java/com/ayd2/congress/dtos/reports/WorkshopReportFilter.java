package com.ayd2.congress.dtos.reports;

import lombok.Value;

@Value
public class WorkshopReportFilter {
    private Long congressId;
    private Long activityId;
}
