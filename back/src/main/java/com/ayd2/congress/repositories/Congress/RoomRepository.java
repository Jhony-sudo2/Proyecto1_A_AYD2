package com.ayd2.congress.repositories.Congress;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayd2.congress.models.Congress.ConferenceRoomEntity;

@Repository
public interface RoomRepository extends JpaRepository<ConferenceRoomEntity,Long>{
    boolean existsByNameAndLocationId(String name, Long locationId);
    boolean existsByNameAndLocationIdAndIdNot(String name, Long locationId, Long id);
    List<ConferenceRoomEntity> findByLocationId(Long locationId);
}
