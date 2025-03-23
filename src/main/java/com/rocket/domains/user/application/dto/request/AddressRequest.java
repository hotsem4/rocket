package com.rocket.domains.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
    @NotBlank(message = "State 값이 비어 있을 수 없습니다.")
    String state,

    @NotBlank(message = "City 값이 비어 있을 수 없습니다.")
    String city,

    @NotBlank(message = "Street 값이 비어 있을 수 없습니다.")
    String street,

    @NotBlank(message = "ZipCode 값이 비어 있을 수 없습니다.")
    String zipCode
) {

}
