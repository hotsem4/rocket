package com.rocket.commons.exception.handler;

import com.rocket.commons.exception.exceptions.AccessDeniedCustomException;
import com.rocket.commons.exception.exceptions.DuplicateEmailException;
import com.rocket.commons.exception.exceptions.DuplicateLikeException;
import com.rocket.commons.exception.exceptions.DuplicateNicknameException;
import com.rocket.commons.exception.exceptions.InvalidEmailFormatException;
import com.rocket.commons.exception.exceptions.InvalidTokenException;
import com.rocket.commons.exception.exceptions.LikeNotFoundException;
import com.rocket.commons.exception.exceptions.LoginFailedException;
import com.rocket.commons.exception.exceptions.PostNotFoundException;
import com.rocket.commons.exception.exceptions.RedisOperationException;
import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.commons.exception.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

  @ExceptionHandler(LikeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateLike(LikeNotFoundException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            ErrorResponse.of(409, "게시물을 누르지 않았습니다.", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            ErrorResponse.of(409, "이미 사용중인 Email입니다.", ex.getMessage(),
                request.getRequestURI())
        );
  }

  @ExceptionHandler(DuplicateNicknameException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateNicknameException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            ErrorResponse.of(409, "이미 사용중인 닉네임입니다.", ex.getMessage(),
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

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(400, "요청 헤더가 누락되었습니다.", ex.getMessage(), request.getRequestURI()));
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getDefaultMessage())
        .findFirst()
        .orElse("요청 값이 올바르지 않습니다.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(400, "요청 값이 올바르지 않습니다.", errorMessage, request.getRequestURI()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(400, "요청 값이 올바르지 않습니다.", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
      HttpMediaTypeNotSupportedException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .body(ErrorResponse.of(415, "지원하지 않는 Content-Type입니다.", ex.getMessage(),
            request.getRequestURI()));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException e) {
    return ResponseEntity.badRequest().body("Missing required parameter: " + e.getParameterName());
  }

  @ExceptionHandler(InvalidEmailFormatException.class)
  public ResponseEntity<ErrorResponse> handleInvalidEmailFormatException(
      InvalidEmailFormatException ex,
      HttpServletRequest request
  ) {
    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        "올바르지 않은 이메일 형식입니다.",
        ex.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedCustomException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedCustom(AccessDeniedCustomException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse.of(403, "권한이 없습니다.", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of(500, "서버 내부에서 오류가 발생하였습니다.", ex.getMessage(),
            request.getRequestURI()));
  }


}