package com.ninjamap.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents geographical coordinates (latitude and longitude).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates {

	private double latitude;
	private double longitude;

	@Override
	public String toString() {
		return String.format("%.8f,%.8f", latitude, longitude);
	}
}
