package daehoon.footballv2.devicenotification.service.impl;

import daehoon.footballv2.devicenotification.dto.request.FcmPushRequest;
import daehoon.footballv2.devicenotification.service.FcmPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoopFcmPushService implements FcmPushService {
    @Override
    public void sendToMember(Long memberId, FcmPushRequest request) {
        log.warn(
                "FCM 전송 비활성화: memberId={}, type={}, referenceId={}",
                memberId,
                request.type(),
                request.referenceId()
        );

    }
}
