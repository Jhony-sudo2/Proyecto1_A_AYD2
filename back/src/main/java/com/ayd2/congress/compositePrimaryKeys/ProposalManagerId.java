package com.ayd2.congress.compositePrimaryKeys;

import java.io.Serializable;
import java.util.Objects;

public class ProposalManagerId implements Serializable{
    private Long proposalId;
    private Long userId;

    public ProposalManagerId() {
    }

    public ProposalManagerId(Long proposalId, Long userId) {
        this.proposalId = proposalId;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ProposalManagerId))
            return false;
        ProposalManagerId that = (ProposalManagerId) o;
        return Objects.equals(proposalId, that.proposalId)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proposalId, userId);
    }
}
