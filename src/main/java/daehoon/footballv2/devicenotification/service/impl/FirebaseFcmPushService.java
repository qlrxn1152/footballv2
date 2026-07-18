package daehoon.footballv2.devicenotification.service.impl;

import com.google.firebase.messaging.*;
import daehoon.footballv2.devicenotification.dto.request.FcmPushRequest;
import daehoon.footballv2.devicenotification.service.FcmPushService;
import daehoon.footballv2.devicenotification.service.MemberDeviceTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "firebase",
        name = "enabled",
        havingValue = "true"
)
public class FirebaseFcmPushService implements FcmPushService {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberDeviceTokenService memberDeviceTokenService;

    @Override
    public void sendToMember(
            Long memberId,
            FcmPushRequest request
    ) {
        List<String> tokens =
                memberDeviceTokenService.findTokensByMemberId(memberId);

        for (String token : tokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(request.title())
                                    .setBody(request.body())
                                    .build()
                    )
                    .putData("type", request.type().name())
                    .putData(
                            "referenceId",
                            String.valueOf(request.referenceId())
                    )
                    .build();

            try {
                firebaseMessaging.send(message);

                log.info(
                        "FCM 전송 성공: memberId={}, type={}, referenceId={}",
                        memberId,
                        request.type(),
                        request.referenceId()
                );
            } catch (FirebaseMessagingException exception) {
                log.error(
                        "FCM 전송 실패: memberId={}, type={}, errorCode={}",
                        memberId,
                        request.type(),
                        exception.getMessagingErrorCode()
                );

                if (exception.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    memberDeviceTokenService.deleteInvalidToken(token);

                    log.info("사용 불가능한 FCM 토큰 삭제 : memberId = {}", memberId);
                }
            }
        }
    }
}