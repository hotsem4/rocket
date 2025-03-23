package com.rocket.domains.user.application.dto.response;

import org.springframework.security.core.userdetails.UserDetails;

public record LoginResponse(String token, UserDetails userDetails) {

}
