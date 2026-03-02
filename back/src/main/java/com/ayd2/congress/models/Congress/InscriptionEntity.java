package com.ayd2.congress.models.Congress;

import com.ayd2.congress.compositePrimaryKeys.AttendeeId;
import com.ayd2.congress.models.Pay.PaymentEntity;
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
@Table(name = "inscriptions")
@Data
@NoArgsConstructor
public class InscriptionEntity {

    @EmbeddedId
    private AttendeeId id;

    @MapsId("congressId")
    @ManyToOne
    @JoinColumn(name = "congress_id")
    private CongressEntity congress;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @MapsId("attendeeRolId")
    @ManyToOne
    @JoinColumn(name = "attendee_rol_id")
    private AttendeeRol attendeeRol;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;
}
