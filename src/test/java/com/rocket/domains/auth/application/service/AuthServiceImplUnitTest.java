package com.rocket.domains.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rocket.commons.exception.exceptions.DuplicateEmailException;
import com.rocket.commons.exception.exceptions.InvalidTokenException;
import com.rocket.commons.exception.exceptions.LoginFailedException;
import com.rocket.commons.security.jwt.JwtProvider;
import com.rocket.commons.security.jwt.JwtResolver;
import com.rocket.domains.auth.application.dto.response.TokenResponse;
import com.rocket.domains.auth.domain.repository.RefreshTokenStore;
import com.rocket.domains.user.application.dto.request.AddressRequest;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.domain.entity.Address;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Gender;
import com.rocket.domains.user.domain.enums.Role;
import com.rocket.domains.user.domain.facade.UserFacade;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceImplUnitTest {

  private UserFacade userFacade;
  private JwtProvider jwtProvider;
  private JwtResolver jwtResolver;
  private PasswordEncoder passwordEncoder;
  private RefreshTokenStore refreshTokenStore;

  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    userFacade = mock(UserFacade.class);
    jwtProvider = mock(JwtProvider.class);
    jwtResolver = mock(JwtResolver.class);
    passwordEncoder = mock(PasswordEncoder.class);
    refreshTokenStore = mock(RefreshTokenStore.class);

    authService = new AuthServiceImpl(userFacade, jwtProvider, jwtResolver, passwordEncoder,
        refreshTokenStore);
  }

  @Test
  @DisplayName("회원가입에 성공하면 access, refresh 토큰이 반환된다.")
  void registerSuccess() {
    // given
    UserRegisterRequest dto = new UserRegisterRequest(
        "test@rocket.com", "password", "25",
        Gender.MALE, new AddressRequest("서울", "강남", "테헤란로", "12345"), "rocket","01000000000", Role.USER, null
    );

    User user = User.createWithIdForTest(1L, dto.email(), dto.password(), 25, Gender.MALE,
        new Address("서울", "강남", "테헤란로", "12345"), dto.nickname(),"01000000000" ,Role.USER, null);

    when(userFacade.registerUser(dto)).thenReturn(user);
    when(jwtProvider.createAccessToken(dto.email())).thenReturn("accessToken");
    when(jwtProvider.createRefreshToken(dto.email())).thenReturn("refreshToken");

    // when
    TokenResponse result = authService.register(dto);

    // then
    assertThat(result.accessToken()).isEqualTo("accessToken");
    assertThat(result.refreshToken()).isEqualTo("refreshToken");
    assertThat(result.message()).contains(dto.email());
    verify(refreshTokenStore).save(eq(dto.email()), anyString());
  }

  @Test
  @DisplayName("이메일 또는 비밀번호가 틀리면 LoginFailedException이 발생한다.")
  void loginFail() {
    String email = "test@rocket.com";
    String password = "wrong";

    when(userFacade.findUserByEmail(email)).thenReturn(Optional.empty());

    assertThrows(LoginFailedException.class, () -> authService.login(email, password));
  }

  @Test
  @DisplayName("유효하지 않은 RefreshToken으로 accessReissue를 요청하면 예외가 발생한다.")
  void accessReissueInvalidToken() {
    when(jwtProvider.validateToken("badToken")).thenReturn(false);

    assertThrows(InvalidTokenException.class, () -> authService.accessReissue("badToken"));
  }

  @Test
  @DisplayName("이메일 중복 체크 시 이미 존재하면 DuplicateEmailException 발생한다.")
  void existsByEmailThrowsException() {
    String email = "test@rocket.com";
    when(userFacade.existsByEmail(email)).thenReturn(true);

    assertThrows(DuplicateEmailException.class, () -> authService.existsByEmail(email));
  }

  @Test
  @DisplayName("login() - refreshToken 재발급 조건 분기 테스트")
  void loginShouldIssueNewRefreshTokenWhenTtlLow() {
    // given
    String email = "test@rocket.com";
    String rawPassword = "password";
    String encodedPassword = "encodedPassword";
    String accessToken = "accessToken";
    String newRefreshToken = "newRefreshToken";

    User user = User.createWithIdForTest(1L, email, encodedPassword, 25, Gender.MALE,
        new Address("서울", "강남", "테헤란로", "12345"), "nickname", "01000000000", Role.USER, null);

    when(userFacade.findUserByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
    when(jwtProvider.createAccessToken(email)).thenReturn(accessToken);
    when(refreshTokenStore.getExpire(email)).thenReturn(100L); // 100초 남음
    when(jwtProvider.createRefreshToken(email)).thenReturn(newRefreshToken);

    // when
    TokenResponse response = authService.login(email, rawPassword);

    // then
    assertThat(response.accessToken()).isEqualTo(accessToken);
    assertThat(response.refreshToken()).isEqualTo(newRefreshToken);
    verify(refreshTokenStore).save(email, newRefreshToken);
  }

  @Test
  @DisplayName("accessReissue() - refreshToken은 유효하지만 redis 값과 일치하지 않으면 예외")
  void accessReissueFailsWhenRefreshTokenMismatch() {
    // given
    String refreshToken = "clientToken";
    String storedToken = "serverToken";
    String email = "test@rocket.com";

    when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
    when(jwtProvider.isRefreshTokenValid(refreshToken)).thenReturn(true);
    when(jwtProvider.getEmail(refreshToken)).thenReturn(email);
    when(refreshTokenStore.get(email)).thenReturn(storedToken);

    // then
    assertThrows(InvalidTokenException.class, () -> authService.accessReissue(refreshToken));
  }

  @Test
  @DisplayName("logout() - 유효하지 않은 AccessToken 입력 시 예외 발생")
  void logoutFailsForInvalidToken() {
    // given
    String token = "invalidToken";
    when(jwtProvider.isAccessTokenValid(token)).thenReturn(false);

    // then
    assertThrows(InvalidTokenException.class, () -> authService.logout(token));
  }

  @Test
  @DisplayName("authenticate() - 비밀번호가 일치하지 않으면 예외 발생")
  void authenticateFailsOnWrongPassword() {
    // given
    String email = "test@rocket.com";
    String correctPassword = "encodedPassword";
    String wrongPassword = "wrongPassword";

    User user = User.createWithIdForTest(1L, email, correctPassword, 25, Gender.MALE,
        new Address("서울", "강남", "테헤란로", "12345"), "nickname", "01000000000", Role.USER, null);

    when(userFacade.findUserByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(wrongPassword, correctPassword)).thenReturn(false);

    // then
    assertThrows(LoginFailedException.class, () -> authService.authenticate(email, wrongPassword));
  }

  @Test
  @DisplayName("authenticate() - 이메일이 존재하지 않으면 dummyPassword 비교 후 예외 발생")
  void authenticateFailsWhenEmailNotFound() {
    // given
    String email = "nonexistent@rocket.com";
    String rawPassword = "password";

    when(userFacade.findUserByEmail(email)).thenReturn(Optional.empty());

    // then
    assertThrows(LoginFailedException.class, () ->
        authService.authenticate(email, rawPassword)
    );

    // dummyPassword 비교가 호출되었는지 검증
    verify(passwordEncoder).matches(eq(rawPassword), anyString());
  }

  @Test
  @DisplayName("refreshReissue() - Redis TTL이 충분하면 RefreshToken을 재발급하지 않는다.")
  void refreshReissueWithoutRenewal() {
    // given
    String email = "test@rocket.com";
    String accessToken = "accessToken";
    String existingRefreshToken = "existingRefreshToken";

    when(refreshTokenStore.get(email)).thenReturn(existingRefreshToken);
    when(jwtProvider.validateToken(existingRefreshToken)).thenReturn(true);
    when(jwtProvider.isRefreshTokenValid(existingRefreshToken)).thenReturn(true);
    when(jwtProvider.createAccessToken(email)).thenReturn(accessToken);
    when(refreshTokenStore.getExpire(email)).thenReturn(60 * 60 * 24 * 3L); // 3일 TTL

    // when
    TokenResponse response = authService.refreshReissue(email,
        false); // shouldRefreshToken == false

    // then
    assertThat(response.accessToken()).isEqualTo(accessToken);
    assertThat(response.refreshToken()).isEqualTo(existingRefreshToken);
    verify(refreshTokenStore, never()).save(any(), any());
  }

  @Test
  @DisplayName("register() - 회원가입 시 RefreshToken이 Redis에 저장된다.")
  void registerShouldSaveRefreshTokenInRedis() {
    // given
    String email = "test@rocket.com";
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    AddressRequest address = new AddressRequest("서울", "강남", "테헤란로", "12345");
    UserRegisterRequest request = new UserRegisterRequest(
        email, "password", "25", Gender.MALE, address, "nickname", "01000000000", Role.USER, null);

    User savedUser = User.createWithIdForTest(1L, email, "encodedPassword", 25,
        Gender.MALE, new Address("서울", "강남", "테헤란로", "12345"), "nickname", "01000000000", Role.USER, null);

    when(userFacade.registerUser(request)).thenReturn(savedUser);
    when(jwtProvider.createAccessToken(email)).thenReturn(accessToken);
    when(jwtProvider.createRefreshToken(email)).thenReturn(refreshToken);

    // when
    TokenResponse response = authService.register(request);

    // then
    assertThat(response.accessToken()).isEqualTo(accessToken);
    assertThat(response.refreshToken()).isEqualTo(refreshToken);
    verify(refreshTokenStore).save(email, refreshToken);
  }


}
