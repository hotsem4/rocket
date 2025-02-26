package com.rocket.commons.exception.exceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String email) {
    super(String.format("이메일이 %s인 유저 정보를 찾을 수 없습니다.", email));
  }
}
