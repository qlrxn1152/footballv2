package daehoon.footballv2.devicenotification.event;

import daehoon.footballv2.devicenotification.dto.request.FcmPushRequest;
import daehoon.footballv2.devicenotification.service.FcmPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchAcceptedPushEventListener {

    private final FcmPushService fcmPushService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleMatchAcceptedPush(
            MatchAcceptedPushEvent event
    ) {
        FcmPushRequest request = new FcmPushRequest(
                event.title(),
                event.body(),
                event.type(),
                event.teamMatchId()
        );

        fcmPushService.sendToMember(
                event.receiverMemberId(),
                request
        );
    }
}