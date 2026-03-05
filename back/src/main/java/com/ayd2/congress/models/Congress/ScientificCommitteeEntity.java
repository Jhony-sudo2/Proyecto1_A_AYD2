package com.ayd2.congress.models.Congress;

import com.ayd2.congress.compositePrimaryKeys.CommiteeId;
import com.ayd2.congress.models.User.UserEntity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "scientific_committee" )
public class ScientificCommitteeEntity {
    @EmbeddedId
    private CommiteeId id;
    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private UserEntity user;
    @MapsId("congressId")
    @ManyToOne
    @JoinColumn(name = "congress_id",nullable = false)
    private CongressEntity congress;
}
