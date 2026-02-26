package com.ayd2.congress.models.Congress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conferenceRoom")
@Data
@NoArgsConstructor
public class ConferenceRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationEntity location;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private long capacity;
    @Column(nullable = false)
    private String description;

}
