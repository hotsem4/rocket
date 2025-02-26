package com.rocket.commons.exception.exceptions;

public class PostNotFoundException extends RuntimeException {
  public PostNotFoundException(String message) {
    super(String.format("%s인 id를 갖고 있는 post를 찾을 수 없습니다.", message));
  }
}
