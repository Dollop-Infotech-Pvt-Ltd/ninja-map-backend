package com.ninjamap.app.service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ninjamap.app.utils.BlockCodeDecoder;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to monitor memory usage and prevent memory leaks
 */
@Service
@Slf4j
public class MemoryMonitoringService {

    @Autowired
    private BlockCodeDecoder blockCodeDecoder;
    
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    
    // Memory threshold percentages
    private static final double WARNING_THRESHOLD = 0.80; // 80%
    private static final double CRITICAL_THRESHOLD = 0.90; // 90%
    
    /**
     * Monitor memory usage every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void monitorMemoryUsage() {
        try {
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            long usedMemory = heapUsage.getUsed();
            long maxMemory = heapUsage.getMax();
            
            if (maxMemory > 0) {
                double usagePercentage = (double) usedMemory / maxMemory;
                
                if (usagePercentage > CRITICAL_THRESHOLD) {
                    log.error("CRITICAL: Memory usage at {:.1f}% ({} MB / {} MB)", 
                             usagePercentage * 100, 
                             usedMemory / (1024 * 1024), 
                             maxMemory / (1024 * 1024));
                    
                    // Emergency cleanup
                    performEmergencyCleanup();
                    
                } else if (usagePercentage > WARNING_THRESHOLD) {
                    log.warn("WARNING: Memory usage at {:.1f}% ({} MB / {} MB)", 
                            usagePercentage * 100, 
                            usedMemory / (1024 * 1024), 
                            maxMemory / (1024 * 1024));
                    
                    // Preventive cleanup
                    performPreventiveCleanup();
                    
                } else {
                    log.debug("Memory usage normal: {:.1f}% ({} MB / {} MB)", 
                             usagePercentage * 100, 
                             usedMemory / (1024 * 1024), 
                             maxMemory / (1024 * 1024));
                }
            }
            
        } catch (Exception e) {
            log.error("Error monitoring memory usage", e);
        }
    }
    
    /**
     * Perform emergency cleanup when memory is critically high
     */
    private void performEmergencyCleanup() {
        log.info("Performing emergency memory cleanup...");
        
        try {
            // Clear all caches
            blockCodeDecoder.clearCaches();
            
            // Force garbage collection
            System.gc();
            Thread.sleep(100); // Give GC time to work
            System.gc();
            
            log.info("Emergency cleanup completed");
            
        } catch (Exception e) {
            log.error("Error during emergency cleanup", e);
        }
    }
    
    /**
     * Perform preventive cleanup when memory usage is high
     */
    private void performPreventiveCleanup() {
        log.info("Performing preventive memory cleanup...");
        
        try {
            // Get cache stats before cleanup
            var statsBefore = blockCodeDecoder.getCacheStats();
            
            // Clear caches if they're getting large
            if (statsBefore.get("lgaCacheSize") > 5000 || 
                statsBefore.get("blockCodeCacheSize") > 25000) {
                blockCodeDecoder.clearCaches();
                log.info("Cleared caches - LGA: {}, BlockCode: {}", 
                        statsBefore.get("lgaCacheSize"), 
                        statsBefore.get("blockCodeCacheSize"));
            }
            
            // Suggest garbage collection
            System.gc();
            
        } catch (Exception e) {
            log.error("Error during preventive cleanup", e);
        }
    }
    
    /**
     * Get current memory statistics
     */
    public MemoryStats getMemoryStats() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        
        return new MemoryStats(
            heapUsage.getUsed(),
            heapUsage.getMax(),
            heapUsage.getCommitted(),
            nonHeapUsage.getUsed(),
            blockCodeDecoder.getCacheStats()
        );
    }
    
    /**
     * Memory statistics data class
     */
    public static class MemoryStats {
        private final long heapUsed;
        private final long heapMax;
        private final long heapCommitted;
        private final long nonHeapUsed;
        private final java.util.Map<String, Integer> cacheStats;
        
        public MemoryStats(long heapUsed, long heapMax, long heapCommitted, 
                          long nonHeapUsed, java.util.Map<String, Integer> cacheStats) {
            this.heapUsed = heapUsed;
            this.heapMax = heapMax;
            this.heapCommitted = heapCommitted;
            this.nonHeapUsed = nonHeapUsed;
            this.cacheStats = cacheStats;
        }
        
        public double getHeapUsagePercentage() {
            return heapMax > 0 ? (double) heapUsed / heapMax : 0;
        }
        
        public String getFormattedHeapUsage() {
            return String.format("%.1f%% (%d MB / %d MB)", 
                               getHeapUsagePercentage() * 100,
                               heapUsed / (1024 * 1024),
                               heapMax / (1024 * 1024));
        }
        
        // Getters
        public long getHeapUsed() { return heapUsed; }
        public long getHeapMax() { return heapMax; }
        public long getHeapCommitted() { return heapCommitted; }
        public long getNonHeapUsed() { return nonHeapUsed; }
        public java.util.Map<String, Integer> getCacheStats() { return cacheStats; }
    }
}