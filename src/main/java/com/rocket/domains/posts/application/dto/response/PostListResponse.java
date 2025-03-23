package com.rocket.domains.posts.application.dto.response;

import java.time.LocalDateTime;

public record PostListResponse(
    Long id,
    String title,
    String authorName, // 또는 email / nickname
    LocalDateTime createdAt
) {

}
