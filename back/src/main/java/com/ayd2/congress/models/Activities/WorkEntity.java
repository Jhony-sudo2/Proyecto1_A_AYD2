package com.ayd2.congress.models.Activities;

import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.User.UserEntity;

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
@Table(name = "work")
@Data
@NoArgsConstructor
public class WorkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "congress_id")
    private CongressEntity congress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    private boolean state;
    @ManyToOne
    @JoinColumn(name = "type_id")
    private ActivityTypesEntity type;

}
