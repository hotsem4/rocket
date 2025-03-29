package com.rocket.domains.auth.domain.repository;

public interface RefreshTokenStore {

  void save(String email, String refreshToken);

  String get(String email);

  void delete(String email);

  Long getExpire(String email);

}
