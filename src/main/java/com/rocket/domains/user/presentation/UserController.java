package com.rocket.domains.user.presentation;

import com.rocket.domains.user.application.dto.request.LoginRequest;
import com.rocket.domains.user.application.dto.request.UserRegisterRequest;
import com.rocket.domains.user.application.dto.request.UserUpdateRequest;
import com.rocket.domains.user.application.dto.response.UserInfoResponse;
import com.rocket.domains.user.domain.entity.User;
import com.rocket.domains.user.domain.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "사용자 관련 API")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/save")
  @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
  public ResponseEntity<String> postSaveUser(@Valid @RequestBody UserRegisterRequest dto) {
    User user = userService.saveUser(dto);
    if (user != null) {
      return ResponseEntity.ok("계정 생성에 성공하였습니다.");
    } else {
      return ResponseEntity.ok("계정 생성에 실패하였습니다.");
    }
  }

  @PostMapping("/login")
  @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인을 시도합니다.")
  public ResponseEntity<UserInfoResponse> loginUser(@Valid @RequestBody LoginRequest request) {
    UserInfoResponse loginResult = userService.loginUser(request.email(), request.password());
    return ResponseEntity.ok(loginResult);
  }

  /**
   * 관리자만 사용가능한 기능
   *
   * @param email
   * @return ResponseEntity.ok(userInfoResponse)
   */
  @GetMapping("/{email}")
  @Operation(summary = "이메일로 사용자 조회", description = "이메일을 기반으로 사용자를 조회합니다.")
  public ResponseEntity<UserInfoResponse> getUser(@PathVariable String email) {
    UserInfoResponse userInfoResponse = userService.findByEmail(email);
    return ResponseEntity.ok(userInfoResponse);
  }

  @GetMapping
  @Operation(summary = "전체 사용자 조회", description = "전체 사용자를 조회합니다.")
  public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
    List<UserInfoResponse> allUsers = userService.findAllUsers();
    return ResponseEntity.ok(allUsers);
  }

  @PutMapping("/update")
  @Operation(summary = "사용자 수정", description = "사용자 정보를 수정합니다.")
  public ResponseEntity<UserInfoResponse> putUpdateUser(@Valid @RequestBody UserUpdateRequest dto) {
    UserInfoResponse userInfoResponse = userService.updateByEmail(dto);
    return ResponseEntity.ok(userInfoResponse);
  }

  @DeleteMapping("/{email}")
  @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
  public ResponseEntity<String> deleteUser(@PathVariable String email) {
    userService.deleteByEmail(email);
    return ResponseEntity.ok(String.format("%s 계정이 삭제되었습니다.", email));
  }
}
