package com.rocket.domains.auth.application.dto.response;

public record TokenResponse(String accessToken, String refreshToken, String message) {

}
