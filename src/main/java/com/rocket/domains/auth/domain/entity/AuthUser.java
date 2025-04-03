package com.rocket.domains.auth.domain.entity;

import com.rocket.domains.user.domain.enums.Role;

public record AuthUser(Long id, String email, String password, Role role) {

}
