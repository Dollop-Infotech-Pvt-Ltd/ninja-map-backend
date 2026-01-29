package com.ninjamap.app.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor          
@AllArgsConstructor
public class LGA {

    private int id;
    private String stateName;
    private String stateCode;
    private String lgaName;
    private String lgaCode;

    private List<List<Double>> boundary;

    private ZoneName zoneName;
    private ZoneCode zoneCode;

    /**
     * Ray-casting algorithm
     */
    public boolean isPointInside(double latitude, double longitude) {
        if (boundary == null || boundary.size() < 3) {
            return false;
        }

        boolean inside = false;
        int j = boundary.size() - 1;

        for (int i = 0; i < boundary.size(); i++) {
            double xi = boundary.get(i).get(0); // longitude
            double yi = boundary.get(i).get(1); // latitude
            double xj = boundary.get(j).get(0);
            double yj = boundary.get(j).get(1);

            boolean intersect =
                ((yi > latitude) != (yj > latitude)) &&
                (longitude < (xj - xi) * (latitude - yi) / (yj - yi) + xi);

            if (intersect) inside = !inside;
            j = i;
        }

        return inside;
    }
}

enum ZoneCode {
    NC, NE, NW, SE, SS, SW
}

enum ZoneName {
    NorthCentral, NorthEast, NorthWest, SouthEast, SouthSouth, SouthWest
}
