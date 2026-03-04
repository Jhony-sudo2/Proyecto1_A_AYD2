package com.ayd2.congress.dtos.acitivty;

import com.ayd2.congress.models.Enums.ActivityType;
import lombok.Value;

@Value
public class NewProposalRequest {
    private Long congressId;
    private Long userId;
    private String name;
    private String description;
    private ActivityType type;
}
