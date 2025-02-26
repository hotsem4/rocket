package com.rocket.domains.user.application.service;

import com.rocket.commons.exception.exceptions.UserNotFoundException;
import com.rocket.domains.user.application.dto.response.UserDTO;
import com.rocket.domains.user.application.dto.request.UserRegisterDTO;
import com.rocket.domains.user.application.dto.request.UserUpdateDTO;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.repository.UserRepository;
import com.rocket.domains.user.domain.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public Boolean saveUser(UserRegisterDTO dto) {
    return userRepository.saveUser(dto.toEntity());
  }

  @Override
  public UserDTO findByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));
    return UserDTO.from(user);
  }

  /**
   * JdbcTemplate를 사용할 경우 paging기법과 offset을 사용해서
   * OOM을 방지해야 하지만 시간 관계상 구현하지 않음...
   */
  @Override
  public List<UserDTO> findAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(UserDTO::from)
        .toList();
  }

  @Override
  @Transactional
  public UserDTO updateByEmail(UserUpdateDTO dto) {

    boolean isUpdated = false;

    if (!userRepository.existsByEmail(dto.email())) {
      throw new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다.");
    }

    if (dto.age() != null) {
      userRepository.updateAge(dto.email(), dto.age());
      isUpdated = true;
    }
    if (dto.gender() != null) {
      userRepository.updateGender(dto.email(), dto.gender());
      isUpdated = true;
    }
    if (dto.address() != null) {
      userRepository.updateAddress(dto.email(), dto.address());
      isUpdated = true;
    }
    if (!isUpdated) {
      throw new IllegalArgumentException("변경한 내용이 없습니다. 다시 확인해주세요.");
    }

    return findByEmail(dto.email());
  }

  @Override
  public boolean deleteByEmail(String email) {
     if(!userRepository.existsByEmail(email)) {
       throw new UserNotFoundException(email);
     }

    return userRepository.deleteUser(email);
  }
}
