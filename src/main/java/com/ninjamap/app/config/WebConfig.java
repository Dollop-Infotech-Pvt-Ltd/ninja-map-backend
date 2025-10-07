package com.ninjamap.app.config;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class WebConfig implements WebMvcConfigurer {

	private final TrimStringArgumentResolver trimStringArgumentResolver;

	public WebConfig(TrimStringArgumentResolver trimStringArgumentResolver) {
		this.trimStringArgumentResolver = trimStringArgumentResolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(trimStringArgumentResolver);
	}
}
