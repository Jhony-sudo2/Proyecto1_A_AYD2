package com.ayd2.congress.dtos.acitivty;

import com.ayd2.congress.models.Enums.ProposalState;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class UpdateProposal {
    @NotNull
    private ProposalState state;
}
