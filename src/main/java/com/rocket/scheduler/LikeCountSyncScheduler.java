package com.rocket.scheduler;

import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostReader;
import com.rocket.domains.posts.infrastructure.redis.PostLikeRedisService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class LikeCountSyncScheduler {

  //  private final PostRepository postRepository;
  private final PostReader postReader;
  private final PostLikeRedisService postLikeRedisService;

  @Scheduled(fixedRate = 60000) // 매 1분마다 실행
  public void syncLikeCount() {
    List<Post> posts = postReader.findAll();
    for (Post post : posts) {
      int redisLikeCount = postLikeRedisService.getLikeCount(post.getId());
      if (redisLikeCount > 0) {
        // 엔티티에 likeCount 추가 후 Dirty Checking으로 DB에 반영
        post.addLikeCount(redisLikeCount);
        log.info("Post Id {}: Redis likeCount {} -> Updated LikeCount = {}", post.getId(),
            redisLikeCount, post.getLikeCount());

        postLikeRedisService.resetLikeCount(post.getId());
      }
    }
  }
}
