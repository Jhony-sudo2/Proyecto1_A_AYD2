package com.ayd2.congress.models.Attendance;

import com.ayd2.congress.compositePrimaryKeys.AttendanceId;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Enums.AttendanceType;
import com.ayd2.congress.models.User.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
public class AttendanceEntity {
    @EmbeddedId
    private AttendanceId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private UserEntity user;

    @MapsId("activityId")
    @ManyToOne
    @JoinColumn(name = "activity_id",nullable = false)
    private ActivityEntity activity;
    @Column(nullable = false)
    private AttendanceType type;
}
