package com.ayd2.congress.dtos.reports;

import lombok.Value;

@Value
public class EarningCongressRaw {
    Long congressId;
    String congressName;
    double total;
    double commission;
    double earning;
}
