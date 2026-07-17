package daehoon.footballv2.notification.controller;

import daehoon.footballv2.notification.dto.response.MemberNotificationResponse;
import daehoon.footballv2.notification.dto.response.UnreadNotificationCountResponse;
import daehoon.footballv2.notification.service.MemberNotificationService;
import daehoon.footballv2.security.jwt.LoginMember;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberNotificationController {

    private final MemberNotificationService memberNotificationService;


    @GetMapping("/api/notifications")
    public ResponseEntity<List<MemberNotificationResponse>> findMyNotifications(@Parameter(hidden = true) @LoginMember Long memberId) {
        List<MemberNotificationResponse> response = memberNotificationService.findMyNotifications(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/notifications/unread-count")
    public ResponseEntity<UnreadNotificationCountResponse> findUnreadNotificationCount(@Parameter(hidden = true) @LoginMember Long memberId) {
        UnreadNotificationCountResponse response = memberNotificationService.findUnreadNotificationCount(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/api/notifications/{notificationId}/read")
    public ResponseEntity<Void> readNotification(@Parameter(hidden = true) @LoginMember Long memberId, @PathVariable Long notificationId) {
        memberNotificationService.readNotification(memberId, notificationId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
