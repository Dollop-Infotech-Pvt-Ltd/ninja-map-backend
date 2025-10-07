package com.ninjamap.app.security.filter;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.github.benmanes.caffeine.cache.Cache;
import com.ninjamap.app.config.RateLimiterConfig;
import com.ninjamap.app.utils.constants.AppConstants;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * IP-based rate limiting filter using Bucket4j. Limits requests per IP per URL
 * based on configured Bandwidth. Does NOT set rate-limit headers in the
 * response.
 */
@Component
@RequiredArgsConstructor
public class IpRateLimitFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(IpRateLimitFilter.class);

	private final Map<String, Bandwidth> urlRateLimits;
	private final Cache<String, Bucket> ipBucketCache;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String path = req.getRequestURI();

		// Skip rate limiting for OPTIONS requests (CORS preflight)
		if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
			chain.doFilter(request, response);
			return;
		}

		// Match URL pattern
		String matchedKey = urlRateLimits.keySet().stream().filter(url -> pathMatcher.match(url, path)).findFirst()
				.orElse(null);

		if (matchedKey != null) {
			String clientIp = extractIp(req);
			String cacheKey = clientIp + ":" + matchedKey;

			// Get or create a bucket for this IP + URL
			Bucket bucket = ipBucketCache.get(cacheKey,
					k -> RateLimiterConfig.newBucket(urlRateLimits.get(matchedKey)));

			// Consume a token and block if exceeded
			if (!bucket.tryConsume(1)) {
				log.warn("Rate limit exceeded for IP [{}] on path [{}]", clientIp, path);
				res.setStatus(429);
				res.getWriter().write(AppConstants.TOO_MANY_REQUESTS + " IP: " + clientIp);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	/**
	 * Extract client IP from X-Forwarded-For header or remote address.
	 */
	private String extractIp(HttpServletRequest request) {
		String xfHeader = request.getHeader("X-Forwarded-For");
		return xfHeader != null ? xfHeader.split(",")[0].trim() : request.getRemoteAddr();
	}
}
