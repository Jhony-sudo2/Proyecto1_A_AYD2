package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.ProposalResponse;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Activities.ProposalEntity;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    @Mapping(target = "name", source = "proposal.name")
    @Mapping(target = "description", source = "proposal.description")
    ActivityResponse toActivityResponse(ActivityEntity entity);
    List<ActivityResponse> toActivityResponseList(List<ActivityEntity> entities);

    @Mapping(target = "congressName",source = "congress.name")
    @Mapping(target = "userName",source = "user.name")
    ProposalResponse toProposalResponse(ProposalEntity entity);
    List<ProposalResponse> toProposalResponseList(List<ProposalEntity> entities);


}
