package com.rocket.domains.user.application.service.passwordReset;

import com.rocket.commons.exception.exceptions.InvalidTokenException;
import com.rocket.commons.utils.ResetTokenUtil;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 해당 코드는 Redis에서 resetToken을 관리하기 위해 만들어진 클래스이다. redis에 저장하는 기능 유효한 토큰인지 검증하는 기능 토큰을 제거하는 기능 을
 * 제공한다.
 */
@Service
@RequiredArgsConstructor
public class ResetTokenManager {

  private final RedisTemplate<String, String> redisTemplate;

  private static final Duration TOKEN_EXPIRY = Duration.ofMinutes(5);
  private final ResetTokenUtil resetTokenUtil;

  /**
   * 여기 넘어온 token은 암호화가 됐을까? redis의 토큰은 암호화 X
   *
   * @param userId
   * @param token
   */
  public void storeToken(Long userId, String token) {
    String key = getKey(token);
    redisTemplate.opsForValue().set(key, String.valueOf(userId), TOKEN_EXPIRY);
  }

  public Long validateToken(String encryptedToken) throws Exception {
    String rawToken = resetTokenUtil.decrypt(encryptedToken);
    String key = getKey(rawToken);
    String userId = redisTemplate.opsForValue().get(key);
    if (userId == null) {
      throw new InvalidTokenException("유효하지 않거나 만료된 토큰입니다.");
    }
    clearToken(rawToken); // 내부적 토큰 삭제
    return Long.parseLong(userId);
  }

  public void clearToken(String token) {
    redisTemplate.delete(getKey(token));
  }

  private String getKey(String token) {
    return "reset-token:" + token;
  }
}