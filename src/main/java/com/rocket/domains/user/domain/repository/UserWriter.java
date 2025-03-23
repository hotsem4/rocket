package com.rocket.domains.user.domain.repository;

import com.rocket.domains.user.domain.entity.User;

public interface UserWriter {

  User saveUser(User user);

  Boolean deleteUser(String email);
}