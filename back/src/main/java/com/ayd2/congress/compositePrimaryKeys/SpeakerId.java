package com.ayd2.congress.compositePrimaryKeys;

import java.io.Serializable;
import java.util.Objects;

public class SpeakerId implements Serializable{
    private Long activityId;
    private Long userId;

    public SpeakerId() {
    }

    public SpeakerId(Long proposalId, Long userId) {
        this.activityId = proposalId;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getProposalId() {
        return activityId;
    }

    public void setProposalId(Long proposalId) {
        this.activityId = proposalId;
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
        if (!(o instanceof SpeakerId))
            return false;
        SpeakerId that = (SpeakerId) o;
        return Objects.equals(activityId, that.activityId)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId, userId);
    }
}
