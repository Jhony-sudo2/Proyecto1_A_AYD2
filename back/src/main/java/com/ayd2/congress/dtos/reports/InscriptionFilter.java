package com.ayd2.congress.dtos.reports;

import lombok.Value;

@Value
public class InscriptionFilter {
    private Long congressId;
    private Long atteendeType;
}
