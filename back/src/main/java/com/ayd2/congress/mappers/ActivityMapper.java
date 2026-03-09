package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.NewActivityGuest;
import com.ayd2.congress.dtos.acitivty.NewActivityRequest;
import com.ayd2.congress.dtos.acitivty.NewProposalRequest;
import com.ayd2.congress.dtos.acitivty.ProposalResponse;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Activities.ProposalEntity;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "room",ignore = true)
    @Mapping(target = "proposal",ignore = true)
    @Mapping(target = "state",ignore = true)
    @Mapping(target = "attendances",ignore = true)
    ActivityEntity toEntity(NewActivityRequest request);
    
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "room",ignore = true)
    @Mapping(target = "proposal",ignore = true)
    @Mapping(target = "state",ignore = true)
    @Mapping(target = "attendances",ignore = true)
    ActivityEntity toEntity(NewActivityGuest request);

    @Mapping(target = "description", source = "proposal.description")
    @Mapping(target = "type",source = "proposal.type")
    @Mapping(target = "roomId",source = "room.id")
    ActivityResponse toActivityResponse(ActivityEntity entity);
    List<ActivityResponse> toActivityResponseList(List<ActivityEntity> entities);

    @Mapping(target = "congressName",source = "congress.name")
    @Mapping(target = "userName",source = "user.name")
    ProposalResponse toProposalResponse(ProposalEntity entity);
    List<ProposalResponse> toProposalResponseList(List<ProposalEntity> entities);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "congress",ignore = true)
    @Mapping(target = "user",ignore = true)
    @Mapping(target ="state",ignore = true)
    ProposalEntity toProposalEntity(NewProposalRequest request);
}
