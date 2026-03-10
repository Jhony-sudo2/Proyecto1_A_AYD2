package com.ayd2.congress.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ayd2.congress.dtos.Inscription.InscriptionResponse;
import com.ayd2.congress.dtos.Inscription.PayResponse;
import com.ayd2.congress.models.Congress.InscriptionEntity;
import com.ayd2.congress.models.Pay.PaymentEntity;

@Mapper(componentModel = "spring")
public interface InscriptionMapper {
    @Mapping(target="congressName",source ="congress.name")
    @Mapping(target = "congressId",source = "congress.id")
    @Mapping(target = "userId",source = "user.id")
    @Mapping(target = "userName",source = "user.name")
    @Mapping(target = "attendeeRolName",source = "attendeeRol.name")
    InscriptionResponse toInscriptionResponse(InscriptionEntity entity);
    List<InscriptionResponse> toInscriptionResponseList(List<InscriptionEntity> entities);

    @Mapping(target="congressName",source ="congress.name")
    @Mapping(target = "congressId",source = "congress.id")
    @Mapping(target = "userId",source = "user.id")
    @Mapping(target = "userName",source = "user.name")
    PayResponse toPayResponse(PaymentEntity entity);
    List<PayResponse> toPayResponseList(List<PaymentEntity> entities);
}
