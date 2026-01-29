package com.ninjamap.app.payload.response;

import java.util.List;

import com.ninjamap.app.model.Coordinates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a grid cell with polyline data for mapping/visualization
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GridPolyline {
    private int rowIndex;
    private int colIndex;
    private List<Coordinates> polyline; // Closed polygon (5 points: 4 corners + closing point)
    private Coordinates center;
    
    /**
     * Get cell identifier
     */
    public String getCellId() {
        return "R" + rowIndex + "C" + colIndex;
    }
    
    /**
     * Get polyline as comma-separated string for easy export
     */
    public String getPolylineString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < polyline.size(); i++) {
            Coordinates coord = polyline.get(i);
            sb.append(coord.getLatitude()).append(",").append(coord.getLongitude());
            if (i < polyline.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
}