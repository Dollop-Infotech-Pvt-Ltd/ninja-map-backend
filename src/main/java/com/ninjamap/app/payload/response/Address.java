package com.ninjamap.app.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

    private String county;
    private String state;

    @JsonProperty("ISO3166-2-lvl4")
    private String iso3166Lvl4;

    private String postcode;
    private String country;

    @JsonProperty("country_code")
    private String countryCode;
}
