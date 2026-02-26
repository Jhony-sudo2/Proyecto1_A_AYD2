package com.ayd2.congress.compositePrimaryKeys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AttendeeId implements Serializable {

    @Column(name = "congress_id")
    private Long congressId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "attendee_rol_id")
    private Long attendeeRolId;

    public AttendeeId() {
    }

    public AttendeeId(Long congressId, Long userId, Long attendeeRolId) {
        this.congressId = congressId;
        this.userId = userId;
        this.attendeeRolId = attendeeRolId;
    }

    public Long getCongressId() {
        return congressId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAttendeeRolId() {
        return attendeeRolId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AttendeeId))
            return false;
        AttendeeId that = (AttendeeId) o;
        return Objects.equals(congressId, that.congressId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(attendeeRolId, that.attendeeRolId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(congressId, userId, attendeeRolId);
    }
}
