package com.ayd2.congress.dtos.reports;

import java.time.LocalDate;

import lombok.Value;

@Value
public class EarningFilter {
    LocalDate startDate;
    LocalDate endDate;
    Long organizationId;
}
