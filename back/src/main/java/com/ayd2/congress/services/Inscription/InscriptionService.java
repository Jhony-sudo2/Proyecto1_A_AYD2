package com.ayd2.congress.services.Inscription;

import java.util.List;

import com.ayd2.congress.dtos.Inscription.InscriptionResponse;
import com.ayd2.congress.dtos.Inscription.PayRequest;
import com.ayd2.congress.dtos.Inscription.PayResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InsufficientFundsException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Congress.InscriptionEntity;
import com.ayd2.congress.models.Pay.PaymentEntity;

public interface InscriptionService {
    void enroll(Long userId,Long rolId,Long congressId) throws NotFoundException,DuplicatedEntityException;
    PayResponse pay(PayRequest payRequest) throws NotFoundException,InsufficientFundsException,DuplicatedEntityException;
    List<PayResponse> getPaymentsByUserId(Long userId) throws NotFoundException;
    List<InscriptionResponse> getInscriptionsByUserId(Long userId) throws NotFoundException;
    List<InscriptionResponse> getInscriptionsByCongressId(Long congressId) throws NotFoundException;
    boolean isUserEnrolledInCongress(Long userId, Long congressId) throws NotFoundException;
    PaymentEntity getPayById(Long id) throws NotFoundException;
    InscriptionEntity getInscriptionById(Long id) throws NotFoundException;
}
