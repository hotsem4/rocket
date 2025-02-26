package com.rocket.domains.user.domain.enums;

public enum Gender {
  MALE, FEMALE;

  public static Gender fromString(String gender) {
    return Gender.valueOf(gender.toUpperCase());
  }
}
