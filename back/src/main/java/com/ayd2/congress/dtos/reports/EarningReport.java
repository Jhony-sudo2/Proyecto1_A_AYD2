package com.ayd2.congress.dtos.reports;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class EarningReport {
    private String congressName;
    private LocalDateTime endDate;
    private LocalDateTime startDate;
    private String locationName;
    private String organizationName;
    private Double totalCollected;
    private Double totalProfit;
    public EarningReport(String congressName, LocalDateTime endDate, LocalDateTime startDate,
                         String locationName, String organizationName,
                         Double totalCollected, Double totalProfit) {
        this.congressName = congressName;
        this.endDate = endDate;
        this.startDate = startDate;
        this.locationName = locationName;
        this.organizationName = organizationName;
        this.totalCollected = totalCollected;
        this.totalProfit = totalProfit;
    }
    
}
