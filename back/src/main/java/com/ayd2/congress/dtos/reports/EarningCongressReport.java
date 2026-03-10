package com.ayd2.congress.dtos.reports;

import com.ayd2.congress.dtos.Congress.CongressResponse;

import lombok.Value;

@Value
public class EarningCongressReport {
    private CongressResponse congress;
    private double total;
    private double commission;
    private double earning;
}
