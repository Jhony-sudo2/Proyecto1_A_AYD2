package com.ayd2.congress.repositories.Congress;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.models.Congress.ConferenceRoomEntity;

public interface RoomRepository extends JpaRepository<ConferenceRoomEntity,Long>{
    
}
