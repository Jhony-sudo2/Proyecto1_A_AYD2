package com.ayd2.congress.compositePrimaryKeys;

import java.io.Serializable;
import java.util.Objects;

import com.ayd2.congress.models.Enums.AttendanceType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AttendanceId implements Serializable {
    private Long userId;
    private Long activityId;
    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", nullable = false, length = 30)
    private AttendanceType type;

    public AttendanceId() {
    }
    public AttendanceId(Long userId, Long activityId, AttendanceType type) {
        this.userId = userId;
        this.activityId = activityId;
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public AttendanceType getType() {
        return type;
    }

    public void setType(AttendanceType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AttendanceId))
            return false;
        AttendanceId that = (AttendanceId) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(activityId, that.activityId)
                && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, activityId, type);
    }

}
