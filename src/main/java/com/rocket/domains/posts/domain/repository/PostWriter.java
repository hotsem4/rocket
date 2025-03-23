package com.rocket.domains.posts.domain.repository;

import com.rocket.domains.posts.domain.entity.Post;

public interface PostWriter {

  Post savePost(Post post);

  void deleteById(Long id);
}