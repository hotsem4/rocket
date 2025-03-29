package com.rocket.commons.exception.exceptions;

public class DuplicateEmailException extends RuntimeException {

  public DuplicateEmailException(String message) {
    super(String.format("%s는 이미 사용중인 이메일 입니다.", message));
  }

}
