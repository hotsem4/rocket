package com.rocket.domains.auth.domain.repository;

import com.rocket.domains.auth.domain.entity.AuthUser;
import java.util.Optional;

public interface AuthUserReader {

  Optional<AuthUser> getAuthUserByEmail(String email);
}
