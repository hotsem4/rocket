package com.rocket.domains.posts.domain.repository;

import com.rocket.domains.posts.domain.entity.Post;
import java.util.List;
import java.util.Optional;

public interface PostReader {

  Optional<Post> findById(Long id);

  List<Post> findByTitle(String title);

  List<Post> findAll();

  boolean existsById(Long id);
}