package com.rocket.commons.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocket.commons.exception.exceptions.AccessDeniedCustomException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=utf-8");

    String message = "관리자의 권한이 필요합니다.";
    String body = objectMapper.writeValueAsString(new AccessDeniedCustomException(message));
    response.getWriter().write(body);
  }
}
