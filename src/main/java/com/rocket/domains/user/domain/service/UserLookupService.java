package com.rocket.domains.user.domain.service;

import com.rocket.domains.user.domain.entity.User;
import java.util.Optional;

public interface UserLookupService {

  Boolean existsById(Long id);

  Optional<User> findById(Long id);
}
