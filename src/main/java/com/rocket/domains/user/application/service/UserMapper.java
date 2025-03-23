package com.rocket.domains.user.application.service;

import com.rocket.domains.user.application.dto.request.AddressRequest;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.domain.entity.Address;
import com.rocket.domains.user.domain.entity.User;

public class UserMapper {

  public static User toEntity(UserRegisterRequest dto, String password) {
    return User.create(
        dto.email(),
        password,
        Integer.parseInt(dto.age()),
        dto.gender(),
        toAddress(dto.address()),
        dto.nickname()
    );

  }

  private static Address toAddress(AddressRequest dto) {
    return new Address(
        dto.state(),
        dto.city(),
        dto.street(),
        dto.zipCode()
    );
  }

  public static UserInfoResponse fromUser(User user) {
    return new UserInfoResponse(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getAge(),
        user.getGender(),
        user.getAddress()
    );
  }
}
