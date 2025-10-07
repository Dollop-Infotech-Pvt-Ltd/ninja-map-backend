package com.ninjamap.app.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public class TrimStringArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(String.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

		String value = null;

		if (parameter.hasParameterAnnotation(RequestParam.class)) {
			RequestParam rp = parameter.getParameterAnnotation(RequestParam.class);
			value = webRequest.getParameter(rp.name());
		} else if (parameter.hasParameterAnnotation(PathVariable.class)) {
			PathVariable pv = parameter.getParameterAnnotation(PathVariable.class);
			value = webRequest.getParameter(pv.name());
		}

		return value != null ? value.trim() : null;
	}
}
