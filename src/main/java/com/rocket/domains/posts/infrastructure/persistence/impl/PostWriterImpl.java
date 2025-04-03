package com.rocket.domains.posts.infrastructure.persistence.impl;

import com.rocket.domains.posts.domain.entity.Post;
import com.rocket.domains.posts.domain.repository.PostWriter;
import com.rocket.domains.posts.infrastructure.persistence.jpa.PostJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Log4j2
public class PostWriterImpl implements PostWriter {

  private final PostJpaRepository postJpaRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Post savePost(Post post) {
    log.info("[savePost] Post ID: {}, likeCount: {}", post.getId(), post.getLikeCount());
    Post saved = postJpaRepository.save(post);
    entityManager.flush();
    log.info("[savePost] Saved complete");
    return saved;

  }

  @Override
  public void deleteById(Long id) {
    postJpaRepository.deleteById(id);
  }
}
