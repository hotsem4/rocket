package com.rocket.commons.exception.exceptions;

public class RedisOperationException extends RuntimeException {

  public RedisOperationException(String message) {
    super(message);
  }
}