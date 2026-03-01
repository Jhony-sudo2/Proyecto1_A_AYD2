package com.ayd2.congress.dtos.Location;

import lombok.Value;

@Value
public class LocationResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String country;

}
