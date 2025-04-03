package com.rocket.domains.auth.presentation;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocket.commons.exception.exceptions.DuplicateEmailException;
import com.rocket.domains.auth.application.dto.response.TokenResponse;
import com.rocket.domains.auth.domain.service.AuthService;
import com.rocket.domains.user.application.dto.request.AddressRequest;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.domain.enums.Gender;
import com.rocket.domains.user.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RegisterController.class)
@Import(RegisterControllerTest.TestConfig.class)
class RegisterControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private AuthService authService;
  @Autowired
  private ObjectMapper objectMapper;

  @TestConfiguration
  static class TestConfig {

    @Bean
    public AuthService authService() {
      return mock(AuthService.class);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      return http.csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .build();
    }
  }

  private UserRegisterRequest createValidRequest() {
    return new UserRegisterRequest(
        "test@rocket.com",
        "123456",
        "25",
        Gender.MALE,
        new AddressRequest("서울시", "강남구", "역삼로 123", "06234"),
        "test_nickname", "01000000000", Role.USER, null
    );
  }

  @Test
  @DisplayName("회원가입 성공 시 200 OK")
  void registerSuccess() throws Exception {
    UserRegisterRequest request = createValidRequest();
    TokenResponse response = new TokenResponse("access-token", "refresh-token",
        "test@rocket.com 계정 생성이 성공하였습니다.");

    when(authService.register(any(UserRegisterRequest.class))).thenReturn(response);

    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.message").value("test@rocket.com 계정 생성이 성공하였습니다."));
  }

  @Test
  @DisplayName("유효하지 않은 이메일 형식으로 회원가입 요청 시 400 Bad Request")
  void registerInvalidEmail() throws Exception {
    UserRegisterRequest request = new UserRegisterRequest(
        "invalid-email",
        "123456",
        "25",
        Gender.MALE,
        new AddressRequest("서울시", "강남구", "역삼로 123", "06234"),
        "nickname", "01000000000", Role.USER, null
    );

    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("요청 값이 올바르지 않습니다."))
        .andExpect(jsonPath("$.message").value("올바른 이메일 형식이 아닙니다."));
  }

  @Test
  @DisplayName("비밀번호가 너무 짧으면 400 Bad Request")
  void registerShortPassword() throws Exception {
    UserRegisterRequest request = new UserRegisterRequest(
        "test@rocket.com",
        "123", // 너무 짧은 비밀번호
        "25",
        Gender.MALE,
        new AddressRequest("서울시", "강남구", "역삼로 123", "06234"),
        "nickname", "01000000000", Role.USER, null
    );

    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("비밀번호는 최소 6글자 이상이어야 합니다."));
  }

  @Test
  @DisplayName("Gender가 null이면 400 Bad Request")
  void registerNullGender() throws Exception {
    String requestJson = """
        {
          "email": "test@rocket.com",
          "password": "123456",
          "age": "25",
          "gender": null,
          "address": {
            "state": "서울시",
            "city" : "강남구",
            "street": "역삼로 123",
            "zipCode": "06234"
          },
          "nickname": "nickname",
          "phoneNumber": "01000100200",
          "role": "USER"
        }
        """;

    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Gender 값이 null일 수 없습니다."));
  }

  @DisplayName("닉네임이 31자를 넘으면 400 Bad Request")
  @Test
  void registerNicknameTooLong() throws Exception {
    String requestJson = """
        {
          "email": "test@rocket.com",
          "password": "123456",
          "age": "25",
          "gender": "MALE",
          "address": {
            "state": "서울특별시",
            "city": "강남구",
            "street": "역삼로 123",
            "zipCode": "06234"
          },
          "nickname": "%s",
          "phoneNumber": "01000200051",
          "role": "USER"
        }
        """.formatted("a".repeat(31));

    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("닉네임은 최대 30자까지 입력 가능합니다."));
  }

  @DisplayName("주소 필드 중 zipCode가 누락되면 400 Bad Request")
  @Test
  void registerMissingZipCode() throws Exception {
    String requestJson = """
        {
          "email": "test@rocket.com",
          "password": "123456",
          "age": "25",
          "gender": "FEMALE",
          "address": {
            "state": "서울특별시",
            "city": "강남구",
            "street": "역삼로 123"
          },
          "nickname": "닉네임",
          "phoneNumber": "01000200051",
          "role": "USER"
        }
        """;

    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("ZipCode 값이 비어 있을 수 없습니다."));
  }

  @DisplayName("중복된 이메일이면 409 Conflict")
  @Test
  void registerDuplicateEmail() throws Exception {
    // given
    String requestJson = """
        {
          "email": "duplicate@rocket.com",
          "password": "123456",
          "age": "25",
          "gender": "MALE",
          "address": {
            "state": "서울특별시",
            "city": "강남구",
            "street": "역삼로 123",
            "zipCode": "06234"
          },
          "nickname": "닉네임",
          "phoneNumber": "01000100200",
          "role": "USER"
        }
        """;

    doThrow(new DuplicateEmailException("duplicate@rocket.com"))
        .when(authService).register(any(UserRegisterRequest.class));

    mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("duplicate@rocket.com는 이미 사용중인 이메일 입니다."));

  }

}
