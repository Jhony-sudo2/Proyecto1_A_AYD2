package com.ayd2.congress.dtos.Wallet;
import java.time.LocalDateTime;

import lombok.Value;

@Value
public class RechargeHistory {
    private double amount;
    private LocalDateTime date;
}
