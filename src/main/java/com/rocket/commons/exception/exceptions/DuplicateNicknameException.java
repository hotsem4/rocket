package com.rocket.commons.exception.exceptions;

public class DuplicateNicknameException extends RuntimeException {

  public DuplicateNicknameException(String message) {
    super(String.format("%s는 이미 사용중인 닉네임 입니다.", message));
  }

}
