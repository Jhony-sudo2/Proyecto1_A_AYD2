package com.ayd2.congress.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "systemConfiguration")
@Data
@NoArgsConstructor
public class SystemConfigEntity {
    @Id
    private Long id;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private Double percentage;
}
