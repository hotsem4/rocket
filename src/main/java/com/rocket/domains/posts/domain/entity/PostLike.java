package com.rocket.domains.posts.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PostLike", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"post_id", "user_id"})
})
@Getter
@NoArgsConstructor
public class PostLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(name = "post_id", nullable = false)
  private Long postId;  // ← 여기서 직접 FK(ID)만 저장

  @Column(name = "user_id", nullable = false)
  private Long userId;  // ← User 객체 대신 userId만

  public PostLike(Long postId, Long userId) {
    this.postId = postId;
    this.userId = userId;
  }
}
