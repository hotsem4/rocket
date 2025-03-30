package com.rocket.domains.auth.presentation;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rocket.commons.exception.exceptions.InvalidEmailFormatException;
import com.rocket.domains.auth.domain.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(EmailCheckController.class)
@Import(EmailCheckControllerTest.MockConfig.class)
class EmailCheckControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private AuthService authService;


  @TestConfiguration
  static class SecurityTestConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http
          .csrf(AbstractHttpConfigurer::disable) // <-- CSRF 비활성화
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .build();
    }
  }

  @TestConfiguration
  static class MockConfig {

    @Bean
    public AuthService authService() {
      return mock(AuthService.class);
    }
  }

  @Test
  @DisplayName("이메일 중복 확인 성공 시 204 No Content")
  void checkEmailSuccess() throws Exception {
    doNothing().when(authService).existsByEmail("test@rocket.com");

    mockMvc.perform(MockMvcRequestBuilders.get("/email/check")
            .param("email", "test@rocket.com"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("이메일 파라미터가 없으면 400 Bad Request")
  void checkEmailMissingParam() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/email/check"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("이메일 형식이 잘못되면 400 Bad Request")
  void checkInvalidEmailFormat() throws Exception {
    doThrow(new InvalidEmailFormatException("invalid-email"))
        .when(authService).existsByEmail("invalid-email");

    mockMvc.perform(MockMvcRequestBuilders.get("/email/check")
            .param("email", "invalid-email"))
        .andExpect(status().isBadRequest());
  }
}
