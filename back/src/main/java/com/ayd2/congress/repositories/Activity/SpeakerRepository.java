package com.ayd2.congress.repositories.Activity;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayd2.congress.compositePrimaryKeys.SpeakerId;
import com.ayd2.congress.models.Activities.SpeakerEntity;

public interface SpeakerRepository extends JpaRepository<SpeakerEntity,SpeakerId> {
    
}
