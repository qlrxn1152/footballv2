package daehoon.footballv2.devicenotification.controller;

import daehoon.footballv2.devicenotification.dto.request.MemberDeviceTokenRegisterRequest;
import daehoon.footballv2.devicenotification.service.MemberDeviceTokenService;
import daehoon.footballv2.security.jwt.LoginMember;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberDeviceTokenController {

    private final MemberDeviceTokenService memberDeviceTokenService;

    @PostMapping("/api/device-tokens") // 토큰 등록
    public ResponseEntity<Void> registerDeviceToken(@Parameter(hidden = true) @LoginMember Long memberId, @Valid @RequestBody MemberDeviceTokenRegisterRequest request) {
        memberDeviceTokenService.registerDeviceToken(memberId, request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/api/device-tokens")
    public ResponseEntity<Void> unregisterDeviceToken(@Parameter(hidden = true) @LoginMember Long memberId, @RequestParam String token) {
        memberDeviceTokenService.unregisterDeviceToken(memberId, token);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
