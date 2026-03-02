package com.ayd2.congress.dtos.Inscription;

import lombok.Value;

@Value
public class InscriptionResponse {
    private Long congressId;
    private String congressName;
    private Long userId;
    private String userName;
    private String attendeeRolName;
}
