package com.rocket.domains.user.application.dto.response;

public record UserSimpleInfoResponse(
    Long userId,
    String nickname,
    String profileImageUrl
) {}
