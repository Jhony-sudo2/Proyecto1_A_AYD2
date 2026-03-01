package com.ayd2.congress.dtos.Location;

import lombok.Value;

@Value
public class NewRoomRequest {
    private String name;
    private Integer capacity;
    private String equipment;
    private String description;
}
