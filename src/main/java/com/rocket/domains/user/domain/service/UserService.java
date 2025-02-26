package com.rocket.domains.user.domain.service;

import com.rocket.domains.user.application.dto.response.UserDTO;
import com.rocket.domains.user.application.dto.request.UserRegisterDTO;
import com.rocket.domains.user.application.dto.request.UserUpdateDTO;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
  Boolean saveUser(UserRegisterDTO dto);

  UserDTO findByEmail(String email);

  List<UserDTO> findAllUsers();

  UserDTO updateByEmail(UserUpdateDTO dto);

  boolean deleteByEmail(String email);
}
