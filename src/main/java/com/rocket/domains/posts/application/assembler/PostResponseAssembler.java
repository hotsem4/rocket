package com.rocket.domains.posts.application.assembler;

import com.rocket.domains.posts.application.dto.response.PostDetailInfoResponse;
import com.rocket.domains.posts.application.service.PostMapper;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.infrastructure.redis.PostLikeRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostResponseAssembler {

  private final PostLikeRedisService postLikeRedisService;

  public PostDetailInfoResponse toDetailDto(Post post) {
    int likeCount = postLikeRedisService.getLikeCount(post.getId());
    return PostMapper.toDetailDto(post, likeCount);
  }

}
