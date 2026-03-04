package com.ayd2.congress.compositePrimaryKeys;

import java.util.Objects;

import jakarta.persistence.Column;

public class CommiteeId {
    @Column(name = "congress_id")
    private Long congressId;
    @Column(name = "user_id")
    private Long userId;
    public CommiteeId() {
    }
    public CommiteeId(Long congressId, Long userId) {
        this.congressId = congressId;
        this.userId = userId;
    }
    public Long getCongressId() {
        return congressId;
    }
    public void setCongressId(Long congressId) {
        this.congressId = congressId;
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
        if (!(o instanceof CommiteeId))
            return false;
        CommiteeId that = (CommiteeId) o;
        return Objects.equals(congressId, that.congressId)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(congressId, userId);
    }
}
