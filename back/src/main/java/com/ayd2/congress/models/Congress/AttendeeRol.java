package com.ayd2.congress.models.Congress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendeeRol")
@Data
@NoArgsConstructor
public class AttendeeRol {
    @Id
    private Long id;
    @Column(nullable = false,unique = true)
    private String name;
}
