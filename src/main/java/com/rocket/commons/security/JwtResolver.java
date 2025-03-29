package com.rocket.commons.security;

import org.springframework.stereotype.Component;

@Component
public class JwtResolver {

  public String resolveAccessToken(String accessToken) {
    return resolveBearerToken(accessToken);
  }

  public String resolveRefreshToken(String refreshToken) {
    return resolveBearerToken(refreshToken);
  }

  private String resolveBearerToken(String TokenIncludeBearer) {
    return (TokenIncludeBearer
        != null && TokenIncludeBearer.startsWith("Bearer ")) ? TokenIncludeBearer.substring(7)
        : null;
  }


}
