package com.rocket.domains.auth.infrastructure;

import com.rocket.domains.auth.domain.entity.AuthUser;
import com.rocket.domains.auth.domain.repository.AuthUserReader;
import com.rocket.domains.user.domain.facade.UserFacade;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthUserReaderImpl implements AuthUserReader {

  private final UserFacade userFacade;

  @Override
  public Optional<AuthUser> getAuthUserByEmail(String email) {
    return userFacade.findUserByEmail(email)
        .map(user -> new AuthUser(user.getId(), user.getEmail(), user.getPassword()));
  }
}
