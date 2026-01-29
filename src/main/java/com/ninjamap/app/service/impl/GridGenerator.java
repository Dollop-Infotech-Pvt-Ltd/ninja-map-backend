package com.ninjamap.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.ninjamap.app.model.Coordinates;
import com.ninjamap.app.payload.response.GridCell;
import com.ninjamap.app.payload.response.GridPolyline;
import com.ninjamap.app.utils.BlockCodeDecoder;

/**
 * Optimized Grid Generator for creating 3x3 meter grid cells
 * Features:
 * - Accurate Haversine-based distance calculations
 * - Efficient polygon intersection testing
 * - Parallel processing support for large grids
 * - Memory-optimized data structures
 */
@Service
public class GridGenerator {

    // Earth's radius in meters
    private static final double EARTH_RADIUS = 6371000.0;
    
    // Grid cell size in meters
    private static final double GRID_SIZE_METERS = 3.0;
    
    // Threshold for parallel processing (number of cells)
    private static final int PARALLEL_THRESHOLD = 1000;
    
    /**
     * Generates a 3x3 meter grid within the specified boundary
     * 
     * @param leftBottomLat  Latitude of left bottom corner
     * @param leftBottomLon  Longitude of left bottom corner
     * @param leftTopLat     Latitude of left top corner
     * @param leftTopLon     Longitude of left top corner
     * @param rightTopLat    Latitude of right top corner
     * @param rightTopLon    Longitude of right top corner
     * @param rightBottomLat Latitude of right bottom corner
     * @param rightBottomLon Longitude of right bottom corner
     * @return List of GridCell objects containing polyline data
     */
    public List<GridCell> generateGrid(
            double leftBottomLat, double leftBottomLon,
            double leftTopLat, double leftTopLon,
            double rightTopLat, double rightTopLon,
            double rightBottomLat, double rightBottomLon) {
        
        // Calculate the bounding box
        double minLat = Math.min(Math.min(leftBottomLat, leftTopLat), 
                                 Math.min(rightTopLat, rightBottomLat));
        double maxLat = Math.max(Math.max(leftBottomLat, leftTopLat), 
                                 Math.max(rightTopLat, rightBottomLat));
        double minLon = Math.min(Math.min(leftBottomLon, leftTopLon), 
                                 Math.min(rightTopLon, rightBottomLon));
        double maxLon = Math.max(Math.max(leftBottomLon, leftTopLon), 
                                 Math.max(rightTopLon, rightBottomLon));
        
        // Calculate center point for accurate degree conversion
        double centerLat = (minLat + maxLat) / 2;
        
        // Convert 3 meters to degrees at the center point
        double latDelta = metersToLatitudeDegrees(GRID_SIZE_METERS);
        double lonDelta = metersToLongitudeDegrees(GRID_SIZE_METERS, centerLat);
        
        // Calculate number of rows and columns
        int rows = (int) Math.ceil((maxLat - minLat) / latDelta);
        int cols = (int) Math.ceil((maxLon - minLon) / lonDelta);
        
        // Precompute boundary vertices for efficiency
        double[] boundaryLats = {leftBottomLat, leftTopLat, rightTopLat, rightBottomLat};
        double[] boundaryLons = {leftBottomLon, leftTopLon, rightTopLon, rightBottomLon};
        
        List<GridCell> gridCells = new ArrayList<>();
        
        // Use parallel processing for large grids
        int totalCells = rows * cols;
        if (totalCells > PARALLEL_THRESHOLD) {
            ConcurrentHashMap<String, GridCell> cellMap = new ConcurrentHashMap<>();
            
            IntStream.range(0, rows).parallel().forEach(row -> {
                for (int col = 0; col < cols; col++) {
                    GridCell cell = createGridCell(row, col, minLat, minLon, 
                                                   latDelta, lonDelta, 
                                                   boundaryLats, boundaryLons);
                    if (cell != null) {
                        cellMap.put(row + "_" + col, cell);
                    }
                }
            });
            
            gridCells.addAll(cellMap.values());
        } else {
            // Sequential processing for smaller grids
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    GridCell cell = createGridCell(row, col, minLat, minLon, 
                                                   latDelta, lonDelta, 
                                                   boundaryLats, boundaryLons);
                    if (cell != null) {
                        gridCells.add(cell);
                    }
                }
            }
        }
        
        return gridCells;
    }
    
    /**
     * Create a single grid cell with boundary checking
     */
    private GridCell createGridCell(int row, int col, double minLat, double minLon,
                                    double latDelta, double lonDelta,
                                    double[] boundaryLats, double[] boundaryLons) {
        double cellMinLat = minLat + (row * latDelta);
        double cellMaxLat = cellMinLat + latDelta;
        double cellMinLon = minLon + (col * lonDelta);
        double cellMaxLon = cellMinLon + lonDelta;
        
        // Calculate center first (most important for boundary check)
        double centerLat = (cellMinLat + cellMaxLat) / 2;
        double centerLon = (cellMinLon + cellMaxLon) / 2;
        
        // Check if cell center is within the boundary polygon
        if (!isPointInPolygon(centerLat, centerLon, boundaryLats, boundaryLons)) {
            return null;
        }
        
        // Create cell corners
        Coordinates bottomLeft = new Coordinates(cellMinLat, cellMinLon);
        Coordinates bottomRight = new Coordinates(cellMinLat, cellMaxLon);
        Coordinates topRight = new Coordinates(cellMaxLat, cellMaxLon);
        Coordinates topLeft = new Coordinates(cellMaxLat, cellMinLon);
        Coordinates center = new Coordinates(centerLat, centerLon);
        
        return new GridCell(row, col, bottomLeft, bottomRight, topRight, topLeft, center);
    }
    
    /**
     * Generates grid with polyline format optimized for mapping
     * This is the recommended method for visualization purposes
     */
    public List<GridPolyline> generateGridPolylines(
            double leftBottomLat, double leftBottomLon,
            double leftTopLat, double leftTopLon,
            double rightTopLat, double rightTopLon,
            double rightBottomLat, double rightBottomLon) {
        
        List<GridCell> cells = generateGrid(
            leftBottomLat, leftBottomLon,
            leftTopLat, leftTopLon,
            rightTopLat, rightTopLon,
            rightBottomLat, rightBottomLon
        );
        
        List<GridPolyline> polylines = new ArrayList<>(cells.size());
        
        for (GridCell cell : cells) {
            List<Coordinates> polyline = new ArrayList<>(5);
            polyline.add(cell.getBottomLeft());
            polyline.add(cell.getBottomRight());
            polyline.add(cell.getTopRight());
            polyline.add(cell.getTopLeft());
            polyline.add(cell.getBottomLeft()); // Close the polygon
            
            GridPolyline gridPolyline = new GridPolyline(
                cell.getRowIndex(),
                cell.getColIndex(),
                polyline,
                cell.getCenter()
            );
            
            polylines.add(gridPolyline);
        }
        
        return polylines;
    }
    
    /**
     * Generate grid with block codes for each cell
     * Integrates with existing BlockCodeDecoder
     */
    public List<GridCellWithCode> generateGridWithBlockCodes(
            double leftBottomLat, double leftBottomLon,
            double leftTopLat, double leftTopLon,
            double rightTopLat, double rightTopLon,
            double rightBottomLat, double rightBottomLon,
            BlockCodeDecoder blockCodeDecoder) {
        
        List<GridCell> cells = generateGrid(
            leftBottomLat, leftBottomLon,
            leftTopLat, leftTopLon,
            rightTopLat, rightTopLon,
            rightBottomLat, rightBottomLon
        );
        
        List<GridCellWithCode> cellsWithCodes = new ArrayList<>(cells.size());
        
        for (GridCell cell : cells) {
            String blockCode = blockCodeDecoder.generateUniqueCodeForBlock(cell.getCenter());
            cellsWithCodes.add(new GridCellWithCode(cell, blockCode));
        }
        
        return cellsWithCodes;
    }
    
    /**
     * Convert meters to latitude degrees
     * 1 degree of latitude ≈ 111,320 meters (constant at all latitudes)
     */
    private double metersToLatitudeDegrees(double meters) {
        return meters / 111320.0;
    }
    
    /**
     * Convert meters to longitude degrees at a given latitude
     * Longitude degree distance varies with latitude due to Earth's curvature
     */
    private double metersToLongitudeDegrees(double meters, double latitude) {
        double latRad = Math.toRadians(latitude);
        return meters / (111320.0 * Math.cos(latRad));
    }
    
    /**
     * Optimized point-in-polygon test using ray casting algorithm
     * Uses precomputed vertex arrays for better performance
     */
    private boolean isPointInPolygon(double lat, double lon,
                                    double[] latitudes, double[] longitudes) {
        boolean inside = false;
        int n = latitudes.length;
        int j = n - 1;
        
        for (int i = 0; i < n; i++) {
            double xi = longitudes[i];
            double yi = latitudes[i];
            double xj = longitudes[j];
            double yj = latitudes[j];
            
            // Ray casting algorithm
            if (((yi > lat) != (yj > lat)) &&
                (lon < (xj - xi) * (lat - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
            
            j = i;
        }
        
        return inside;
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     * Useful for validation and testing
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.asin(Math.sqrt(a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * Inner class to hold grid cell with its block code
     */
    public static class GridCellWithCode {
        private final GridCell gridCell;
        private final String blockCode;
        
        public GridCellWithCode(GridCell gridCell, String blockCode) {
            this.gridCell = gridCell;
            this.blockCode = blockCode;
        }
        
        public GridCell getGridCell() {
            return gridCell;
        }
        
        public String getBlockCode() {
            return blockCode;
        }
        
        public Coordinates getCenter() {
            return gridCell.getCenter();
        }
    }
}