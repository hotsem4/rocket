package com.rocket.domains.user.domain.enums;

public enum Role {
  ADMIN, USER;

  public boolean isAdmin() {
    return this == ADMIN;
  }

  public boolean isNotAdmin() {
    return this != ADMIN;
  }
}
