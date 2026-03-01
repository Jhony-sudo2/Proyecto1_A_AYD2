package com.ayd2.congress.dtos.Location;

import lombok.Value;

@Value
public class NewLocationRequest {
    private String name;
    private String address;
    private String city;
    private String country;
}
