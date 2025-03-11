package com.rest.server.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class Location {
    private String locationStreet;
    private String locationCity;
    private String locationState;
    private String locationCountry;
    private String locationTimezone;

    @JsonProperty("street")
    public String getLocationStreet() {
        return locationStreet;
    }

    @JsonProperty("city")
    public String getLocationCity() {
        return locationCity;
    }

    @JsonProperty("state")
    public String getLocationState() {
        return locationState;
    }

    @JsonProperty("country")
    public String getLocationCountry() {
        return locationCountry;
    }

    @JsonProperty("timezone")
    public String getLocationTimezone() {
        return locationTimezone;
    }

}