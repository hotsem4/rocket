package com.rocket.domains.auth.infrastructure.redis;

import com.rocket.commons.exception.exceptions.RedisOperationException;
import com.rocket.domains.auth.domain.repository.RefreshTokenStore;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {

  private final StringRedisTemplate redisTemplate;

  private static final String PREFIX = "refresh:";
  private static final Duration TTL = Duration.ofDays(7);

  @Override
  public void save(String email, String refreshToken) {
    try {
      redisTemplate.opsForValue().set(PREFIX + email, refreshToken, TTL);
    } catch (Exception e) {
      throw new RedisOperationException("Redis에 RefreshToken 저장 실패");
    }
  }

  @Override
  public String get(String email) {
    try {
      return redisTemplate.opsForValue().get(PREFIX + email);
    } catch (Exception e) {
      throw new RedisOperationException("Redis에서 RefreshToken 조회 실패");
    }
  }

  @Override
  public void delete(String email) {
    try {
      redisTemplate.delete(PREFIX + email);
    } catch (Exception e) {
      throw new RedisOperationException("Redis에서 RefreshToken 삭제 실패");
    }
  }

  @Override
  public Long getExpire(String email) {
    try {
      return redisTemplate.getExpire(PREFIX + email, TimeUnit.SECONDS);
    } catch (Exception e) {
      throw new RedisOperationException("Redis에서 TTL 조회 실패");
    }
  }
}