package com.ninjamap.app.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjamap.app.payload.response.ErrorResponse;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, AppConstants.ACCESS_DENIED);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        String errorMessage = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            errorMessage = mapper.writeValueAsString(error);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
        response.getOutputStream().write(bytes);
    }
}
