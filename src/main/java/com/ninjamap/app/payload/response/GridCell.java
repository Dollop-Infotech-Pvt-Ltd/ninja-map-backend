package com.ninjamap.app.payload.response;

import com.ninjamap.app.model.Coordinates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single grid cell with its corner coordinates
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GridCell {
    private int rowIndex;
    private int colIndex;
    private Coordinates bottomLeft;
    private Coordinates bottomRight;
    private Coordinates topRight;
    private Coordinates topLeft;
    private Coordinates center;
    
    /**
     * Get the area of the grid cell in square meters
     */
    public double getAreaSquareMeters() {
        return 3.0 * 3.0; // 3x3 meter grid
    }
    
    /**
     * Get unique identifier for this cell
     */
    public String getCellId() {
        return "R" + rowIndex + "C" + colIndex;
    }
}