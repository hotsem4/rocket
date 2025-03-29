package com.rocket.domains.auth.application.service;

import com.rocket.commons.exception.exceptions.DuplicateEmailException;
import com.rocket.commons.exception.exceptions.InvalidTokenException;
import com.rocket.commons.exception.exceptions.LoginFailedException;
import com.rocket.commons.security.JwtProvider;
import com.rocket.commons.security.JwtResolver;
import com.rocket.domains.auth.application.dto.response.AuthenticatedUser;
import com.rocket.domains.auth.application.dto.response.TokenResponse;
import com.rocket.domains.auth.domain.repository.RefreshTokenStore;
import com.rocket.domains.auth.domain.service.AuthService;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.facade.UserFacade;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

  private final UserFacade userFacade;
  private final JwtProvider jwtProvider;
  private final JwtResolver jwtResolver;
  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenStore redis;

  @Override
  public TokenResponse register(UserRegisterRequest dto) {
    User user = userFacade.registerUser(dto);
    String accessToken = jwtProvider.createAccessToken(dto.email());
    String refreshToken = jwtProvider.createRefreshToken(dto.email());

    redis.save(user.getEmail(), refreshToken);
    String message = String.format("%s 계정 생성이 성공하였습니다.", dto.email());
    return new TokenResponse(accessToken, refreshToken, message);
  }

  @Override
  public TokenResponse login(String email, String password) {
    User authenticatedUser = authenticate(email, password);
    String accessToken = jwtProvider.createAccessToken(authenticatedUser.getEmail());

    Long ttl = redis.getExpire(authenticatedUser.getEmail());
    boolean shouldIssueNewRefreshToken =
        ttl == null || ttl == -2 || ttl == -1 || ttl < 60 * 60 * 24 * 2;
    String refreshToken;
    if (shouldIssueNewRefreshToken) {

      refreshToken = jwtProvider.createRefreshToken(authenticatedUser.getEmail());
      redis.save(authenticatedUser.getEmail(), refreshToken);

    } else {
      refreshToken = redis.get(authenticatedUser.getEmail());
    }

    String message = "로그인에 성공하였습니다";
    return new TokenResponse(accessToken, refreshToken, message);

  }

  @Override
  public User authenticate(String email, String rawPassword) {
    Optional<User> userOptional = userFacade.findUserByEmail(email);
    if (userOptional.isEmpty()) {
      passwordEncoder.matches(rawPassword, "dummyPassword");
      throw new LoginFailedException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    User user = userOptional.get();

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new LoginFailedException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    return user;
  }

  @Override
  public TokenResponse accessReissue(String refreshToken) {

    if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
      throw new InvalidTokenException("유효하지 않은 RefreshToken입니다.");
    }

    if (!jwtProvider.isRefreshTokenValid(refreshToken)) {
      throw new InvalidTokenException("RefreshToken 타입이 아닙니다.");
    }

    String email = jwtProvider.getEmail(refreshToken);

    String savedEmail = redis.get(email);
    if (!refreshToken.equals(savedEmail)) {
      throw new InvalidTokenException("서버에 저장된 refreshToken과 일치하지 않습니다.");
    }

    boolean shouldRefreshToken = shouldRefreshTokenRenewal(email);

    return refreshReissue(email, shouldRefreshToken);

  }

  // RefreshToken 갱신 필요 여부를 확인하는 메서드
  private boolean shouldRefreshTokenRenewal(String email) {
    Long ttl = redis.getExpire(email);
    // Redis에 저장된 RefreshToken의 남은 유효 시간이 2일(172800초) 이하이면 갱신
    return (ttl == null || ttl == -2 || ttl == -1 || ttl < 60 * 60 * 24 * 2);
  }

  @Override
  public TokenResponse refreshReissue(String email, Boolean shouldRefreshToken) {
    String refreshToken = redis.get(email);

    if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
      throw new InvalidTokenException("서버에 저장된 RefreshToken이 존재하지 않거나 만료되었습니다.");
    }

    if (!jwtProvider.isRefreshTokenValid(refreshToken)) {
      throw new InvalidTokenException("RefreshToken 타입이 아닙니다.");
    }

    String accessToken = jwtProvider.createAccessToken(email);

    if (shouldRefreshToken) {
      refreshToken = jwtProvider.createRefreshToken(email);
      redis.save(email, refreshToken);
      log.info("RefreshToken이 만료 임박으로 자동 재발급되었습니다. 사용자: {}", email);
    }

    String message = "AccessToken이 재발급되었습니다.";

    return new TokenResponse(accessToken, refreshToken, message);
  }


  @Override
  public void logout(String accessToken) {
    if (!jwtProvider.isAccessTokenValid(accessToken)) {
      throw new InvalidTokenException("올바른 토큰인지 확인해주세요.");
    }
    redis.delete(accessToken);
  }

  @Override
  public AuthenticatedUser getAuthenticatedUser(String accessToken) {
    return null;
  }

  @Override
  public void existsByEmail(String email) {
    if (userFacade.existsByEmail(email)) { // true -> 중복으로 예외 처리
      throw new DuplicateEmailException(email);
    }
  }
}