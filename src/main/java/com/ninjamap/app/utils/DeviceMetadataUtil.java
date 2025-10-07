package com.ninjamap.app.utils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class DeviceMetadataUtil {

	public String getDeviceType(String userAgent) {
		if (userAgent == null)
			return "Unknown";
		userAgent = userAgent.toLowerCase();
		if (userAgent.contains("mobile"))
			return "Mobile";
		if (userAgent.contains("tablet"))
			return "Tablet";
		if (userAgent.contains("windows") || userAgent.contains("mac") || userAgent.contains("linux"))
			return "Desktop";
		return "Unknown";
	}

	public String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			return ip.split(",")[0];
		}
		return request.getRemoteAddr();
	}

	@Async
	public CompletableFuture<String> getLocationFromIp(String ip) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = "http://ip-api.com/json/" + ip + "?fields=city,country";
			Map<String, String> response = restTemplate.getForObject(url, Map.class);
			if (response != null && response.containsKey("city") && response.containsKey("country")) {
				return CompletableFuture.completedFuture(response.get("city") + ", " + response.get("country"));
			}
		} catch (Exception e) {
			// Log error if needed
		}
		return CompletableFuture.completedFuture("Unknown");
	}
}
