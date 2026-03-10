package com.ayd2.congress.dtos.Inscription;

import java.time.LocalDate;

import lombok.Value;

@Value
public class PayResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long congressId;
    private String congressName;
    private Double total;
    private LocalDate date;
}
