package com.example.muaring.domain.nearby.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LocationRequestDTO {

    @JsonProperty("lat")
    private double latitude;

    @JsonProperty("lng")
    private double longitude;
}

