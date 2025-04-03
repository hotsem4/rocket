package com.rocket.scheduler;

import com.rocket.domains.posts.application.service.PostLikeService;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class LikeCountSyncScheduler {

  private final PostLikeService postLikeSyncService;

  /**
   * 매 분마다 Redis -> DB 동기화
   */
  @Scheduled(fixedRate = 60000)
  public void syncLikeCount() {
    try {
      log.info("[LikeCountSyncScheduler] Start syncing Redis -> DB...");
      postLikeSyncService.syncRedisLikeToDB();
      log.info("[LikeCountSyncScheduler] Finished syncing!");
    } catch (Exception e) {
      log.error("[LikeCountSyncScheduler] Error while syncing: {}", e.getMessage(), e);
    }
  }
}