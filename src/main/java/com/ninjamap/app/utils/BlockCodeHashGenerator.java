package com.ninjamap.app.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BlockCodeHashGenerator {

    private static final String BASE_32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    /**
     * Decodes a geohash string into latitude and longitude ranges.
     * This implementation matches the Android version exactly.
     * 
     * @param hash The geohash string to decode
     * @return Map containing latitude, longitude, and their min/max ranges
     */
    public static Map<String, Double> decode(String hash) {
        if (hash == null || hash.isEmpty()) {
            return null;
        }

        Map<String, Double> result = new HashMap<>();

        // Create character to binary mapping (same as Android)
        Map<Character, String> charToBinary = new HashMap<>();
        String[] binaryValues = {
            "00000", "00001", "00010", "00011", "00100", "00101", "00110", "00111",
            "01000", "01001", "01010", "01011", "01100", "01101", "01110", "01111",
            "10000", "10001", "10010", "10011", "10100", "10101", "10110", "10111",
            "11000", "11001", "11010", "11011", "11100", "11101", "11110", "11111"
        };

        for (int i = 0; i < BASE_32.length(); i++) {
            charToBinary.put(BASE_32.charAt(i), binaryValues[i]);
        }

        // Convert geohash to binary string
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < hash.length(); i++) {
            char c = hash.toLowerCase().charAt(i);  // Ensure lowercase
            String binary = charToBinary.get(c);
            if (binary == null) {
                return null; // Invalid character
            }
            bits.append(binary);
        }

        // Split into latitude and longitude bits
        StringBuilder latBits = new StringBuilder();
        StringBuilder lonBits = new StringBuilder();

        for (int i = 0; i < bits.length(); i++) {
            if (i % 2 == 0) {
                lonBits.append(bits.charAt(i)); // Even indices are longitude
            } else {
                latBits.append(bits.charAt(i)); // Odd indices are latitude
            }
        }

        // Decode latitude
        double latMin = -90.0;
        double latMax = 90.0;

        for (int i = 0; i < latBits.length(); i++) {
            double latMean = (latMin + latMax) / 2;
            if (latBits.charAt(i) == '1') {
                latMin = latMean;
            } else {
                latMax = latMean;
            }
        }

        // Decode longitude
        double lonMin = -180.0;
        double lonMax = 180.0;

        for (int i = 0; i < lonBits.length(); i++) {
            double lonMean = (lonMin + lonMax) / 2;
            if (lonBits.charAt(i) == '1') {
                lonMin = lonMean;
            } else {
                lonMax = lonMean;
            }
        }

        // Calculate center coordinates
        double latitude = (latMin + latMax) / 2;
        double longitude = (lonMin + lonMax) / 2;

        // Return all values (matching Android implementation)
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("latitudeMin", latMin);
        result.put("latitudeMax", latMax);
        result.put("longitudeMin", lonMin);
        result.put("longitudeMax", lonMax);

        return result;
    }

    /**
     * Encodes latitude and longitude into a geohash string.
     * This implementation matches the Android version exactly.
     * 
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param length The desired length of the geohash
     * @return The encoded geohash string
     */
    public static String encode(double latitude, double longitude, int length) {
        String binaryLatitude = getBinary(latitude, -90.0, 90.0);
        String binaryLongitude = getBinary(longitude, -180.0, 180.0);

        String combinedBits = "";
        for (int i = 0; i < binaryLatitude.length(); i++) {
            combinedBits += "" + binaryLongitude.charAt(i) + binaryLatitude.charAt(i);
        }

        String encodedHash = "";
        for (int i = 0; i < combinedBits.length(); i += 5) {
            String chunk = combinedBits.substring(i, i + 5);
            int index = Integer.parseInt(chunk, 2);
            encodedHash += BASE_32.charAt(index);
        }

        return encodedHash.substring(0, length);
    }

    /**
     * Converts a value to binary representation within a given range.
     * 
     * @param value The value to convert
     * @param min The minimum value of the range
     * @param max The maximum value of the range
     * @return Binary string representation
     */
    private static String getBinary(double value, double min, double max) {
        int precision = 25; // Number of bits for precision
        String binary = "";
        for (int i = 0; i < precision; i++) {
            double mean = (min + max) / 2;
            if (value < mean) {
                binary += "0";
                max = mean;
            } else {
                binary += "1";
                min = mean;
            }
        }
        return binary;
    }

    /**
     * Precision levels for geohash encoding.
     */
    public enum Precision {
        TWENTY_FIVE_HUNDRED_KILOMETERS,        // ±2500 km
        SIX_HUNDRED_THIRTY_KILOMETERS,         // ±630 km
        SEVENTY_EIGHT_KILOMETERS,              // ±78 km
        TWENTY_KILOMETERS,                     // ±20 km
        TWENTY_FOUR_HUNDRED_METERS,            // ±2.4 km
        SIX_HUNDRED_TEN_METERS,                // ±0.61 km
        SEVENTY_SIX_METERS,                    // ±0.076 km
        NINETEEN_METERS,                       // ±0.019 km
        TWO_HUNDRED_FORTY_CENTIMETERS,         // ±0.0024 km
        SIXTY_CENTIMETERS,                     // ±0.00060 km
        SEVENTY_FOUR_MILLIMETERS               // ±0.000074 km
    }
}