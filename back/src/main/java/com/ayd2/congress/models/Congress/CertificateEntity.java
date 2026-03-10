package com.ayd2.congress.models.Congress;

import com.ayd2.congress.models.User.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "certificate",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_certificate_user_congress_role",
            columnNames = {"user_id", "congress_id", "rol_id"}
        )
    }
)
@Data
@NoArgsConstructor
public class CertificateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "user_id",nullable = false)
    private UserEntity user;
    @ManyToOne()
    @JoinColumn(name = "congress_id",nullable = false)
    private CongressEntity congress;
    @ManyToOne()
    @JoinColumn(name = "rol_id",nullable = false)
    private AttendeeRol rol;
}
