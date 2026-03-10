package com.ayd2.congress.dtos.acitivty;

import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.ProposalState;

import lombok.Value;

@Value
public class ProposalResponse {
    private Long id;
    private String name;
    private String congressName;
    private String userName;
    private String description;
    private ActivityType type;
    private ProposalState state;
}
