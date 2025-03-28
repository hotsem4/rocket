package com.rocket.commons.exception;

import com.rocket.commons.exception.exceptions.DuplicateEmailException;
import com.rocket.commons.exception.exceptions.DuplicateLikeException;
import com.rocket.commons.exception.exceptions.InvalidTokenException;
import com.rocket.commons.exception.exceptions.LoginFailedException;
import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.commons.exception.exceptions.RedisOperationException;
import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.commons.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(404, "사용자를 찾을 수 없습니다.", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(PostNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(404, "포스트를 찾을 수 없습니다.", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(LoginFailedException.class)
  public ResponseEntity<ErrorResponse> handleLoginFail(LoginFailedException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse.of(401, "인증이 실패하였습니다.", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(DuplicateLikeException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateLike(DuplicateLikeException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            ErrorResponse.of(409, "이미 좋아요를 누른 게시글입니다.", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            ErrorResponse.of(409, "중복되어 해당 이메일을 사용할 수 없습니다.", ex.getMessage(),
                request.getRequestURI())
        );
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ErrorResponse.of(401, "유효하지 않은 토큰입니다.", ex.getMessage(), request.getRequestURI())
        );
  }

  @ExceptionHandler(RedisOperationException.class)
  public ResponseEntity<ErrorResponse> handleInvalidToken(RedisOperationException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ErrorResponse.of(500, "서버 내부 오류로 인해 요청을 처리하지 못했습니다.", ex.getMessage(),
                request.getRequestURI())
        );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(500, "서버 내부에서 오류가 발생하였습니다.", ex.getMessage(),
            request.getRequestURI()));
  }
}