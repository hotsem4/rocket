package com.rocket.domains.user.domain.entity;

public record Address(String state, String city, String street, String zipCode) {
  public Address {
    if (street == null || city == null || state == null || zipCode == null) {
      throw new IllegalArgumentException("모든 필드는 필수입니다.");
    }
  }
}
