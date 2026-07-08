package daehoon.footballv2.auth.controller;

import daehoon.footballv2.auth.dto.LoginRequest;
import daehoon.footballv2.auth.dto.LoginResponse;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.auth.dto.SignupRequest;
import daehoon.footballv2.auth.dto.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController { // 로그인, 회원가입, 비밀번호 검증등에 관한 컨트롤러.

    private final AuthService authService;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse response = authService.signup(signupRequest.getUsername(), signupRequest.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest LoginRequest) {
        LoginResponse response = authService.login(LoginRequest.getUsername(), LoginRequest.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
