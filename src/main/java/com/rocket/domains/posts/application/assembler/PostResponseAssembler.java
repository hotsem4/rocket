package com.rocket.domains.posts.application.assembler;

import com.rocket.domains.posts.application.dto.response.PostDetailInfoResponse;
import com.rocket.domains.posts.application.service.PostLikeService;
import com.rocket.domains.posts.application.service.PostMapper;
import com.rocket.domains.posts.domain.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostResponseAssembler {

  private final PostLikeService postLikeService;

  public PostDetailInfoResponse toDetailDto(Post post) {
    int likeCount = post.getLikeCount() + postLikeService.getRedisLikeCount(post.getId());
    return PostMapper.toDetailDto(post, likeCount);
  }

}
