package com.rocket.commons.exception.response;

import java.time.LocalDateTime;

public record ErrorResponse(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp
) {

  public static ErrorResponse of(int status, String error, String message, String path) {
    return new ErrorResponse(status, error, message, path, LocalDateTime.now());
  }
}
