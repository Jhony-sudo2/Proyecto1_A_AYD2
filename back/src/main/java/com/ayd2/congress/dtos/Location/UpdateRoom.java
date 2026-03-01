package com.ayd2.congress.dtos.Location;

import lombok.Value;

@Value
public class UpdateRoom {
    private Integer capacity;
    private String description;
    private String name;
    private String equipment;
}
