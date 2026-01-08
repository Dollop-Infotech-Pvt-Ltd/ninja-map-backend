package com.ninjamap.app.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapServiceResponse {

    @JsonProperty("place_id")
    private Long placeId;

    @JsonProperty("licence")
    private String licence;

    @JsonProperty("osm_type")
    private String osmType;

    @JsonProperty("osm_id")
    private Long osmId;

    @JsonProperty("lat")
    private String lat;

    @JsonProperty("lon")
    private String lon;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("boundingbox")
    private List<String> boundingBox;

    @JsonProperty("address")
    private Address address;
}
