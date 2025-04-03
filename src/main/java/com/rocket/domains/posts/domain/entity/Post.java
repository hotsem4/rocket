package com.rocket.domains.posts.domain.entity;

import com.rocket.domains.posts.application.dto.request.PostUpdateRequest;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@Table(name = "Post")
@NoArgsConstructor
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", nullable = false)
  @Comment("게시글 제목")
  private String title;

  @Column(name = "content", unique = false, nullable = false)
  @Comment("게시글 내용")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id")
  @Comment("작성자 ID (User 엔티티 참조)")
  private User author;

  @CreationTimestamp
  @Column(nullable = false)
  @Comment("생성일시")
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @Comment("좋아요 수")
  private int likeCount = 0; // 기본값 0, 사용자 변경 불가능

  public static Post create(String title, String content, User author) {
    Post post = new Post();
    post.title = title;
    post.content = content;
    post.author = author;
    return post;
  }

  // 조회 시 사용할 생성자 (id 포함)
  public Post(Long id, String title, String content, LocalDateTime createdAt) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.createdAt = createdAt;
    this.likeCount = 0; // 항상 0으로 초기화
  }

  public void updateTitle(String title) {
    this.title = title;
  }

  public void updateContent(String content) {
    this.content = content;
  }

  // Post.java (도메인 엔티티)
  public void updateFrom(PostUpdateRequest dto) {
    boolean isUpdated = false;

    if (dto.title() != null && !dto.title().trim().isEmpty()) {
      updateTitle(dto.title());
      isUpdated = true;
    }

    if (dto.content() != null && !dto.content().trim().isEmpty()) {
      updateContent(dto.content());
      isUpdated = true;
    }

    if (!isUpdated) {
      throw new IllegalArgumentException("변경할 값이 없습니다.");
    }
  }


  public void addLikeCount(int additionalLikes) {
    this.likeCount += additionalLikes;
  }

  public boolean isOwnedBy(User user) {
    return this.author.getId().equals(user.getId());
  }



  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Post post = (Post) o;
    return likeCount == post.likeCount && Objects.equals(id, post.id)
        && Objects.equals(title, post.title) && Objects.equals(content,
        post.content) && Objects.equals(author, post.author) && Objects.equals(
        createdAt, post.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, author, createdAt, likeCount);
  }
}
