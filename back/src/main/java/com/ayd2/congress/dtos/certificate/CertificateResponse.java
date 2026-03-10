package com.ayd2.congress.dtos.certificate;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class CertificateResponse {
    private String congressName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String locationName;
    private String name;
    private String lastName;
    private LocalDateTime date;
    private String organizationName;
    private String assitantType;
}
