package com.ayd2.congress.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.Inscription.InscriptionResponse;
import com.ayd2.congress.dtos.Inscription.PayRequest;
import com.ayd2.congress.dtos.Inscription.PayResponse;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InsufficientFundsException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.services.Inscription.InscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inscriptions")
public class InscriptionController {
    private final InscriptionService service;

    @Autowired
    public InscriptionController(InscriptionService service) {
        this.service = service;
    }
    
    @PostMapping("/pay")
    public ResponseEntity<PayResponse> pay(@Valid @RequestBody PayRequest request) throws NotFoundException, InsufficientFundsException, DuplicatedEntityException{
        PayResponse response = service.pay(request);
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/pay/{userId}")
    public ResponseEntity<List<PayResponse>> getPaymentsByUserId(@PathVariable Long userId) throws NotFoundException{
        List<PayResponse> response = service.getPaymentsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<InscriptionResponse>> getInscriptionsByUserid(@PathVariable Long userId) throws NotFoundException{
        List<InscriptionResponse> responses = service.getInscriptionsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pay/congress/{congressId}")
    public ResponseEntity<List<PayResponse>> getPaymentsByCongressId(@PathVariable Long congressId) throws NotFoundException{
        List<PayResponse> response = service.getPaymentsByUserId(congressId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/congress/{congressId}")
    public ResponseEntity<List<InscriptionResponse>> getInscriptionsByCongressId(@PathVariable Long congressId) throws NotFoundException{
        List<InscriptionResponse> responses = service.getInscriptionsByCongressId(congressId);
        return ResponseEntity.ok(responses);
    }


}
