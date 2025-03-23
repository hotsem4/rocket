package com.rocket.domains.posts.infrastructure.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeRedisService {
  private final StringRedisTemplate redisTemplate;

  private static final String POST_LIKE_COUNT_KEY_PREFIX = "post:like:";
  private static final Long LIKE_KEY_TTL_MINUTES = 30L;


  /**
   * 좋아요 증가
   * - Redis에 키가 없으면 초기화 후 증가 및 TTL 설정
   * - 키가 있으면 INCR 후 TTL 갱신
   */
  public void incrementLikeCount(Long postId) {
    String key = POST_LIKE_COUNT_KEY_PREFIX + postId;
    Boolean exists = redisTemplate.hasKey(key);
    if (Boolean.FALSE.equals(exists)) {
      // 키 초기화
      redisTemplate.opsForValue().set(key, "0", LIKE_KEY_TTL_MINUTES, TimeUnit.MINUTES);
    }
    // 좋아요 수 증가
    redisTemplate.opsForValue().increment(key);
    // TTL 갱신
    redisTemplate.expire(key, LIKE_KEY_TTL_MINUTES, TimeUnit.MINUTES);
  }
}
