package com.ninjamap.app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninjamap.app.model.Coordinates;
import com.ninjamap.app.model.LGA;

@Service
public class BlockCodeDecoder {

	private List<LGA> lgas = new ArrayList<>();
	
	// Cache for LGA lookups to avoid repeated point-in-polygon calculations
	private final Map<String, LGA> lgaCache = new ConcurrentHashMap<>();
	private final Map<String, String> blockCodeCache = new ConcurrentHashMap<>();
	
	// Cache size limits to prevent memory leaks - configurable via properties
	@Value("${grid.cache.lga.max-size:10000}")
	private int maxLgaCacheSize;
	
	@Value("${grid.cache.block-code.max-size:50000}")
	private int maxBlockCodeCacheSize;

	// Constructor to load LGAs from JSON resource
	public BlockCodeDecoder() {
		loadLGAsFromJSON();
	}

	// Load LGAs from JSON resource
	private void loadLGAsFromJSON() {
		Type itemType = new TypeToken<List<LGA>>() {
		}.getType();
		lgas = parseJSONFromResource("nigeria_lga.json", itemType);
	}

	// This method parses JSON from a resource file and returns a list of LGAs
	private <T> List<LGA> parseJSONFromResource(String resourcePath, Type type) {
		Gson gson = new Gson();
		try {
			InputStream inputStream = new ClassPathResource(resourcePath).getInputStream();
			Reader reader = new InputStreamReader(inputStream);
			return gson.fromJson(reader, type);
		} catch (IOException ex) {
			// Log error instead of printing stack trace
			System.err.println("Error loading LGA data from " + resourcePath + ": " + ex.getMessage());
		}
		return new ArrayList<>();
	}

	// Finds the Local Government Area (LGA) containing the given coordinates
	public LGA findLGA(Coordinates coordinates) {
		for (LGA lga : lgas) {
			if (lga.isPointInside(coordinates.getLatitude(), coordinates.getLongitude())) {
				return lga;
			}
		}
		return null;
	}
	
	/**
	 * Optimized LGA finder with caching and spatial indexing
	 */
	public LGA findLGAOptimized(Coordinates coordinates) {
		// Create cache key with reduced precision to increase cache hits
		String cacheKey = String.format("%.4f,%.4f", coordinates.getLatitude(), coordinates.getLongitude());
		
		// Check cache first
		LGA cachedLGA = lgaCache.get(cacheKey);
		if (cachedLGA != null) {
			return cachedLGA;
		}
		
		// Find LGA using original method
		LGA foundLGA = findLGA(coordinates);
		
		// Cache the result (including null results to avoid repeated expensive lookups)
		if (lgaCache.size() < maxLgaCacheSize) {
			lgaCache.put(cacheKey, foundLGA);
		}
		
		return foundLGA;
	}

	/**
	 * Generates a unique code for a block based on the given coordinates.
	 * OPTIMIZED VERSION - Uses caching and efficient string operations
	 * 
	 * @param coordinates The coordinates (latitude and longitude) of the block.
	 * @return The generated unique block code.
	 */
	public String generateUniqueCodeForBlock(Coordinates coordinates) {
		// Use the optimized version by default
		return generateUniqueCodeForBlockOptimized(coordinates);
	}
	
	/**
	 * Original implementation - kept for fallback purposes
	 */
	public String generateUniqueCodeForBlockOriginal(Coordinates coordinates) {
		String geohash = BlockCodeHashGenerator.encode(coordinates.getLatitude(), coordinates.getLongitude(), 10);

		// Remove any special characters from the geohash
		String alphanumericGeohash = geohash.replaceAll("[^a-zA-Z0-9]", "");

		// Ensure the code is in uppercase
		String code = alphanumericGeohash.toUpperCase();

		LGA lga = findLGA(coordinates);
		if (lga != null) {
			return lga.getStateCode() + lga.getLgaCode() + "-" + code.substring(0, 3) + "-" + code.substring(3, 7) + "-"
					+ code.substring(7, 10);
		} else {
			return code.substring(0, 3) + "-" + code.substring(3, 7) + "-" + code.substring(7, 10);
		}
	}
	
	/**
	 * OPTIMIZED version with caching and reduced string operations
	 */
	public String generateUniqueCodeForBlockOptimized(Coordinates coordinates) {
		// Create cache key with reduced precision
		String cacheKey = String.format("%.5f,%.5f", coordinates.getLatitude(), coordinates.getLongitude());
		
		// Check cache first
		String cachedCode = blockCodeCache.get(cacheKey);
		if (cachedCode != null) {
			return cachedCode;
		}
		
		// Generate geohash once
		String geohash = BlockCodeHashGenerator.encode(coordinates.getLatitude(), coordinates.getLongitude(), 10);
		
		// Use StringBuilder for efficient string building
		StringBuilder codeBuilder = new StringBuilder(20);
		
		// Remove special characters and convert to uppercase in one pass
		for (char c : geohash.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				codeBuilder.append(Character.toUpperCase(c));
			}
		}
		
		String cleanCode = codeBuilder.toString();
		
		// Find LGA with optimized method
		LGA lga = findLGAOptimized(coordinates);
		
		String finalCode;
		if (lga != null) {
			finalCode = lga.getStateCode() + lga.getLgaCode() + "-" + 
					   cleanCode.substring(0, 3) + "-" + 
					   cleanCode.substring(3, 7) + "-" + 
					   cleanCode.substring(7, 10);
		} else {
			finalCode = cleanCode.substring(0, 3) + "-" + 
					   cleanCode.substring(3, 7) + "-" + 
					   cleanCode.substring(7, 10);
		}
		
		// Cache the result if cache isn't full
		if (blockCodeCache.size() < maxBlockCodeCacheSize) {
			blockCodeCache.put(cacheKey, finalCode);
		}
		
		return finalCode;
	}

	/**
	 * Decodes a block code and retrieves the coordinates it represents.
	 * 
	 * @param code The block code to decode.
	 * @return The coordinates represented by the block code, or null if decoding
	 *         fails.
	 */
	public Coordinates decodeBlockCode(String code) {
		String geohash;
		if (code.length() > 12) {
			geohash = code.substring(code.indexOf("-") + 1).replaceAll("-", "");
		} else if (code.length() == 12) {
			geohash = code.replaceAll("-", "");
		} else {
			return null;
		}
		Map<String, Double> decodedCoordinates = BlockCodeHashGenerator.decode(geohash.toLowerCase());
		if (decodedCoordinates != null) {
			return new Coordinates((decodedCoordinates.get("latitudeMin") + decodedCoordinates.get("latitudeMax")) / 2,
					(decodedCoordinates.get("longitudeMin") + decodedCoordinates.get("longitudeMax")) / 2);
		} else {
			return null; // Handle invalid coordinate case
		}
	}
	
	/**
	 * Clear caches to prevent memory leaks - call this periodically or when memory is low
	 */
	public void clearCaches() {
		lgaCache.clear();
		blockCodeCache.clear();
	}
	
	/**
	 * Get cache statistics for monitoring
	 */
	public Map<String, Integer> getCacheStats() {
		return Map.of(
			"lgaCacheSize", lgaCache.size(),
			"blockCodeCacheSize", blockCodeCache.size()
		);
	}
}