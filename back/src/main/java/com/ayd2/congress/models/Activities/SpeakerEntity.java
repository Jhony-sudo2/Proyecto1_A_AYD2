package com.ayd2.congress.models.Activities;

import com.ayd2.congress.compositePrimaryKeys.SpeakerId;
import com.ayd2.congress.models.User.UserEntity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proposal_manager")
@Data
@NoArgsConstructor
public class SpeakerEntity {
    @EmbeddedId
    private SpeakerId id;

    @MapsId("activityId")
    @ManyToOne 
    @JoinColumn(name = "activity_id", nullable = false)  
    private ActivityEntity activity;
    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
