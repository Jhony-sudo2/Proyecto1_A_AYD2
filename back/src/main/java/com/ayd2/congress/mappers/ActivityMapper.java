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
import com.ayd2.congress.models.Activities.SpeakerEntity;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "attendances", ignore = true)
    @Mapping(target = "congress", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "speakers", ignore = true)
    ActivityEntity toEntity(NewActivityRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "attendances", ignore = true)
    @Mapping(target = "congress", ignore = true)
    @Mapping(target = "speakers", ignore = true)
    ActivityEntity toEntity(NewActivityGuest request);

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "speakers", source = "speakers")
    ActivityResponse toActivityResponse(ActivityEntity entity);

    List<ActivityResponse> toActivityResponseList(List<ActivityEntity> entities);

    @Mapping(target = "congressName", source = "congress.name")
    @Mapping(target = "userName", source = "user.name")
    ProposalResponse toProposalResponse(ProposalEntity entity);

    List<ProposalResponse> toProposalResponseList(List<ProposalEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "congress", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "used",ignore = true)
    ProposalEntity toProposalEntity(NewProposalRequest request);

    default String[] map(List<SpeakerEntity> speakers) {
        if (speakers == null) {
            return new String[0];
        }

        return speakers.stream()
                .map(s -> s.getUser().getName() + " " + s.getUser().getLastName()) 
                .toArray(String[]::new);
    }
}
