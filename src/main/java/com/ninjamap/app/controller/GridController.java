package com.ninjamap.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.response.GridCell;
import com.ninjamap.app.payload.response.GridPolyline;
import com.ninjamap.app.service.MemoryMonitoringService;
import com.ninjamap.app.service.impl.GridGenerator;
import com.ninjamap.app.service.impl.GridGenerator.GridCellWithCode;
import com.ninjamap.app.utils.BlockCodeDecoder;

/**
 * REST API Controller for Grid Generation
 */
@RestController
@RequestMapping("/api/grid")
@CrossOrigin(origins = "*")
public class GridController {

    @Autowired
    private GridGenerator gridGenerator;
    
    @Autowired
    private BlockCodeDecoder blockCodeDecoder;
    
    @Autowired
    private MemoryMonitoringService memoryMonitoringService;
    
    /**
     * Generate 3x3 meter grid cells
     * 
     * POST /api/grid/generate
     * 
     * Request Body:
     * {
     *   "leftBottomLat": 6.5244,
     *   "leftBottomLon": 3.3792,
     *   "leftTopLat": 6.5254,
     *   "leftTopLon": 3.3792,
     *   "rightTopLat": 6.5254,
     *   "rightTopLon": 3.3802,
     *   "rightBottomLat": 6.5244,
     *   "rightBottomLon": 3.3802
     * }
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateGrid(@RequestBody GridRequest request) {
        try {
            List<GridCell> gridCells = gridGenerator.generateGrid(
                request.getLeftBottomLat(), request.getLeftBottomLon(),
                request.getLeftTopLat(), request.getLeftTopLon(),
                request.getRightTopLat(), request.getRightTopLon(),
                request.getRightBottomLat(), request.getRightBottomLon()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalCells", gridCells.size());
            response.put("gridCells", gridCells);
            response.put("gridSizeMeters", 3.0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Generate grid with polyline format
     * 
     * POST /api/grid/polylines
     */
    @PostMapping("/polylines")
    public ResponseEntity<?> generateGridPolylines(@RequestBody GridRequest request) {
        try {
            List<GridPolyline> polylines = gridGenerator.generateGridPolylines(
                request.getLeftBottomLat(), request.getLeftBottomLon(),
                request.getLeftTopLat(), request.getLeftTopLon(),
                request.getRightTopLat(), request.getRightTopLon(),
                request.getRightBottomLat(), request.getRightBottomLon()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalCells", polylines.size());
            response.put("polylines", polylines);
            response.put("gridSizeMeters", 3.0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Generate grid with block codes - OPTIMIZED FOR PERFORMANCE & MEMORY
     * 
     * POST /api/grid/polylines-with-codes
     */
    @PostMapping("/polylines-with-codes")
    public ResponseEntity<?> generateGridWithBlockCodes(@RequestBody GridRequest request) {
        // Input validation to prevent unnecessary processing
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body cannot be null"));
        }
        
        // Validate coordinate bounds to prevent excessive grid generation
        if (!isValidCoordinateRange(request)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid coordinate range. Area too large or coordinates out of bounds"));
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Force garbage collection before processing large grids (optional, for memory-constrained environments)
            if (isLargeGridRequest(request)) {
                System.gc();
            }
            
            List<GridCellWithCode> cellsWithCodes = gridGenerator.generateGridWithBlockCodes(
                request.getLeftBottomLat(), request.getLeftBottomLon(),
                request.getLeftTopLat(), request.getLeftTopLon(),
                request.getRightTopLat(), request.getRightTopLon(),
                request.getRightBottomLat(), request.getRightBottomLon(),
                blockCodeDecoder
            );
            
            // Use Map.of for immutable, memory-efficient response (prevents accidental modifications)
            Map<String, Object> response = Map.of(
                "totalCells", cellsWithCodes.size(),
                "cellsWithCodes", cellsWithCodes,
                "gridSizeMeters", 3.0,
                "processingTimeMs", System.currentTimeMillis() - startTime
            );
            
            // Clear any thread-local variables that might cause memory leaks
            clearThreadLocals();
            
            return ResponseEntity.ok(response);
            
        } catch (OutOfMemoryError e) {
            // Handle OOM specifically
            System.gc(); // Force cleanup
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of("error", "Grid area too large. Please reduce the area size.", 
                               "suggestion", "Try smaller coordinate ranges"));
                               
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid input: " + e.getMessage()));
                    
        } catch (Exception e) {
            // Log the error for debugging but don't expose internal details
            System.err.println("Grid generation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error occurred", 
                               "timestamp", System.currentTimeMillis()));
        } finally {
            // Ensure cleanup happens regardless of success/failure
            clearThreadLocals();
        }
    }
    
    /**
     * Validate coordinate range to prevent excessive processing
     */
    private boolean isValidCoordinateRange(GridRequest request) {
        // Check if coordinates are within valid Earth bounds
        if (request.getLeftBottomLat() < -90 || request.getLeftBottomLat() > 90 ||
            request.getLeftTopLat() < -90 || request.getLeftTopLat() > 90 ||
            request.getRightTopLat() < -90 || request.getRightTopLat() > 90 ||
            request.getRightBottomLat() < -90 || request.getRightBottomLat() > 90) {
            return false;
        }
        
        if (request.getLeftBottomLon() < -180 || request.getLeftBottomLon() > 180 ||
            request.getLeftTopLon() < -180 || request.getLeftTopLon() > 180 ||
            request.getRightTopLon() < -180 || request.getRightTopLon() > 180 ||
            request.getRightBottomLon() < -180 || request.getRightBottomLon() > 180) {
            return false;
        }
        
        // Calculate approximate area to prevent excessive grid generation
        double latRange = Math.abs(Math.max(request.getLeftTopLat(), request.getRightTopLat()) - 
                                  Math.min(request.getLeftBottomLat(), request.getRightBottomLat()));
        double lonRange = Math.abs(Math.max(request.getRightTopLon(), request.getRightBottomLon()) - 
                                  Math.min(request.getLeftBottomLon(), request.getLeftTopLon()));
        
        // Prevent grids larger than 0.1 degrees (approximately 11km x 11km)
        // This prevents memory issues with extremely large grids
        return latRange <= 0.1 && lonRange <= 0.1;
    }
    
    /**
     * Check if this is a large grid request that might need memory management
     */
    private boolean isLargeGridRequest(GridRequest request) {
        double latRange = Math.abs(Math.max(request.getLeftTopLat(), request.getRightTopLat()) - 
                                  Math.min(request.getLeftBottomLat(), request.getRightBottomLat()));
        double lonRange = Math.abs(Math.max(request.getRightTopLon(), request.getRightBottomLon()) - 
                                  Math.min(request.getLeftBottomLon(), request.getLeftTopLon()));
        
        // Consider it large if area > 0.01 degrees (approximately 1km x 1km)
        return (latRange * lonRange) > 0.01;
    }
    
    /**
     * Clear thread-local variables to prevent memory leaks
     */
    private void clearThreadLocals() {
        try {
            // Clear any thread-local variables that might be holding references
            Thread currentThread = Thread.currentThread();
            java.lang.reflect.Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            threadLocalsField.set(currentThread, null);
            
            java.lang.reflect.Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
            inheritableThreadLocalsField.set(currentThread, null);
        } catch (Exception e) {
            // Ignore reflection errors - this is just a safety measure
        }
    }
    
    /**
     * Generate block code from coordinates
     * 
     * POST /api/grid/coordinates-to-block-code
     * 
     * Request Body:
     * {
     *   "latitude": 6.5249,
     *   "longitude": 3.3797
     * }
     */
    @PostMapping("/coordinates-to-block-code")
    public ResponseEntity<?> coordinatesToBlockCode(@RequestBody CoordinatesRequest request) {
        try {
            // Validate input coordinates
            if (request.getLatitude() < -90 || request.getLatitude() > 90) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid latitude. Must be between -90 and 90"));
            }
            
            if (request.getLongitude() < -180 || request.getLongitude() > 180) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid longitude. Must be between -180 and 180"));
            }
            
            com.ninjamap.app.model.Coordinates coordinates = 
                new com.ninjamap.app.model.Coordinates(request.getLatitude(), request.getLongitude());
            
            String blockCode = blockCodeDecoder.generateUniqueCodeForBlock(coordinates);
            
            Map<String, Object> response = Map.of(
                "blockCode", blockCode,
                "coordinates", Map.of(
                    "latitude", request.getLatitude(),
                    "longitude", request.getLongitude()
                ),
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Decode block code to coordinates
     * 
     * POST /api/grid/block-code-to-coordinates
     * 
     * Request Body:
     * {
     *   "blockCode": "LAGOS001-ABC-DEFG-HIJ"
     * }
     */
    @PostMapping("/block-code-to-coordinates")
    public ResponseEntity<?> blockCodeToCoordinates(@RequestBody BlockCodeRequest request) {
        try {
            // Validate input block code
            if (request.getBlockCode() == null || request.getBlockCode().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Block code cannot be null or empty"));
            }
            
            com.ninjamap.app.model.Coordinates coordinates = 
                blockCodeDecoder.decodeBlockCode(request.getBlockCode().trim());
            
            if (coordinates == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid block code format or unable to decode"));
            }
            
            Map<String, Object> response = Map.of(
                "coordinates", Map.of(
                    "latitude", coordinates.getLatitude(),
                    "longitude", coordinates.getLongitude()
                ),
                "blockCode", request.getBlockCode().trim(),
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get memory and cache statistics
     * 
     * GET /api/grid/memory-stats
     */
    @GetMapping("/memory-stats")
    public ResponseEntity<?> getMemoryStats() {
        try {
            MemoryMonitoringService.MemoryStats stats = memoryMonitoringService.getMemoryStats();
            
            Map<String, Object> response = Map.of(
                "memoryUsage", Map.of(
                    "heapUsed", stats.getHeapUsed(),
                    "heapMax", stats.getHeapMax(),
                    "heapCommitted", stats.getHeapCommitted(),
                    "nonHeapUsed", stats.getNonHeapUsed(),
                    "usagePercentage", String.format("%.2f%%", stats.getHeapUsagePercentage() * 100),
                    "formattedUsage", stats.getFormattedHeapUsage()
                ),
                "cacheStats", stats.getCacheStats(),
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    
    /**
     * Request DTO for grid generation
     */
    public static class GridRequest {
        private double leftBottomLat;
        private double leftBottomLon;
        private double leftTopLat;
        private double leftTopLon;
        private double rightTopLat;
        private double rightTopLon;
        private double rightBottomLat;
        private double rightBottomLon;
        
        // Getters and Setters
        public double getLeftBottomLat() { return leftBottomLat; }
        public void setLeftBottomLat(double leftBottomLat) { this.leftBottomLat = leftBottomLat; }
        
        public double getLeftBottomLon() { return leftBottomLon; }
        public void setLeftBottomLon(double leftBottomLon) { this.leftBottomLon = leftBottomLon; }
        
        public double getLeftTopLat() { return leftTopLat; }
        public void setLeftTopLat(double leftTopLat) { this.leftTopLat = leftTopLat; }
        
        public double getLeftTopLon() { return leftTopLon; }
        public void setLeftTopLon(double leftTopLon) { this.leftTopLon = leftTopLon; }
        
        public double getRightTopLat() { return rightTopLat; }
        public void setRightTopLat(double rightTopLat) { this.rightTopLat = rightTopLat; }
        
        public double getRightTopLon() { return rightTopLon; }
        public void setRightTopLon(double rightTopLon) { this.rightTopLon = rightTopLon; }
        
        public double getRightBottomLat() { return rightBottomLat; }
        public void setRightBottomLat(double rightBottomLat) { this.rightBottomLat = rightBottomLat; }
        
        public double getRightBottomLon() { return rightBottomLon; }
        public void setRightBottomLon(double rightBottomLon) { this.rightBottomLon = rightBottomLon; }
    }
    
    /**
     * Request DTO for coordinates to block code conversion
     */
    public static class CoordinatesRequest {
        private double latitude;
        private double longitude;
        
        // Getters and Setters
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
    
    /**
     * Request DTO for block code to coordinates conversion
     */
    public static class BlockCodeRequest {
        private String blockCode;
        
        // Getters and Setters
        public String getBlockCode() { return blockCode; }
        public void setBlockCode(String blockCode) { this.blockCode = blockCode; }
    }
}
