package com.rocket.domains.posts.application.service;

import com.rocket.domains.posts.application.dto.request.PostCreateRequest;
import com.rocket.domains.posts.application.dto.response.PostDetailInfoResponse;
import com.rocket.domains.posts.application.dto.response.PostListResponse;
import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.user.domain.entity.User;
import java.util.Objects;

public class PostMapper {

  public static Post toEntity(PostCreateRequest dto, User author) {
    return Post.create(
        dto.title(),
        dto.content(),
        author
    );
  }

  public static PostDetailInfoResponse toDetailDto(Post post) {
    Objects.requireNonNull(post, "Post 객체는 null일 수 없습니다.");
    return new PostDetailInfoResponse(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getAuthor().getId(),
        post.getAuthor().getNickname(),
        post.getCreatedAt(),
        post.getLikeCount()
    );
  }

  // Entity → 목록용 응답 DTO
  public static PostListResponse toListDto(Post post) {
    return new PostListResponse(
        post.getId(),
        post.getTitle(),
        post.getAuthor().getNickname(),
        post.getCreatedAt()
    );
  }
}
