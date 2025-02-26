package com.rocket.domains.posts.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
  private Long id;
  private final String title;
  private final String content;
  private final Long authorId;
  private LocalDateTime createdAt;
  private int likeCount = 0; // 기본값 0, 사용자 변경 불가능

  // 조회 시 사용할 생성자 (id 포함)
  public Post(Long id, String title, String content, Long authorId, LocalDateTime createdAt) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.authorId = authorId;
    this.createdAt = createdAt;
    this.likeCount = 0; // 항상 0으로 초기화
  }

  // 빌더 패턴 적용
  private Post(Builder builder) {
    this.title = builder.title;
    this.content = builder.content;
    this.authorId = builder.authorId;
    this.likeCount = 0; // 항상 0으로 초기화
  }


  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public Long getAuthorId() {
    return authorId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public int getLikeCount() {
    return likeCount;
  }

  // 좋아요 수 증가 (이 메서드를 통해서만 변경 가능)
  public void increaseLikeCount() {
    this.likeCount++;
  }

  // 제목 수정 (불변성 유지)
  public Post updateTitle(String title) {
    return new Post(id, title, content, authorId, createdAt);
  }

  // 빌더 클래스 (likeCount 필드 없음)
  public static class Builder {
    private String title;
    private String content;
    private Long authorId;

    public Builder() {}

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder content(String content) {
      this.content = content;
      return this;
    }

    public Builder authorId(Long authorId) {
      this.authorId = authorId;
      return this;
    }

    public Post build() {
      return new Post(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Post post = (Post) o;
    return likeCount == post.likeCount &&
        Objects.equals(id, post.id) &&
        Objects.equals(title, post.title) &&
        Objects.equals(content, post.content) &&
        Objects.equals(authorId, post.authorId) &&
        Objects.equals(createdAt, post.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, authorId, createdAt, likeCount);
  }
}
