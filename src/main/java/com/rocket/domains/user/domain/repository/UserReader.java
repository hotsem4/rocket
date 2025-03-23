package com.rocket.domains.user.domain.repository;

import com.rocket.domains.user.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserReader {

  Boolean existsById(Long id);

  Optional<User> findById(Long id);

  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);

  Boolean existsByNickname(String nickname);

  List<User> findAll();
}
