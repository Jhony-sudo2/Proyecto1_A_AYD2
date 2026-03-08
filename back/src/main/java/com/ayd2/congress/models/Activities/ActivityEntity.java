package com.ayd2.congress.models.Activities;

import java.time.LocalDateTime;
import java.util.List;

import com.ayd2.congress.models.Attendance.AttendanceEntity;
import com.ayd2.congress.models.Congress.ConferenceRoomEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity")
@Data
@NoArgsConstructor
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDateTime endDate;
    private boolean state = false;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private ConferenceRoomEntity room;
    @ManyToOne
    @JoinColumn(name = "proposal_id")
    private ProposalEntity proposal;
    @Column(nullable = true)
    private long capacity;
    @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AttendanceEntity> attendances;

}
