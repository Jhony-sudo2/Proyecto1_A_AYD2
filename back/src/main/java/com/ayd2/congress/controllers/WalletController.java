package com.ayd2.congress.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.Wallet.RechargeHistory;
import com.ayd2.congress.dtos.Wallet.RechargeRequest;
import com.ayd2.congress.dtos.Wallet.WalletResponse;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.services.Wallet.WalletService;

@RestController
@RequestMapping("/users/{userId}/wallet")
public class WalletController {
    private final WalletService service;

    @Autowired
    public WalletController(WalletService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<WalletResponse> getById(@PathVariable Long userId) throws NotFoundException{
        WalletResponse response  = service.getByUserIdResponse(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<WalletResponse> rechargeWallet(@Validated @RequestBody RechargeRequest request,@PathVariable Long userId) throws NotFoundException{
        WalletResponse response = service.recharge(request,userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recharges")
    public ResponseEntity<List<RechargeHistory>> getHistoryByWalletId(@PathVariable Long userId){
        List<RechargeHistory> response = service.getHistoryRechargeByWalletId(userId);
        return ResponseEntity.ok(response);
    }
}
