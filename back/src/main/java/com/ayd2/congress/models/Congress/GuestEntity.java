package com.ayd2.congress.models.Congress;

import com.ayd2.congress.compositePrimaryKeys.GuestId;
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
@Table(name = "guest")
@Data
@NoArgsConstructor
public class GuestEntity {
    @EmbeddedId
    private GuestId id;

    @MapsId("congressId")
    @ManyToOne
    @JoinColumn(name = "congress_id")
    private CongressEntity congress;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
