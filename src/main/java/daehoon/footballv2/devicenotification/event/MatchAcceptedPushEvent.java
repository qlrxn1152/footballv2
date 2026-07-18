package daehoon.footballv2.devicenotification.event;

import daehoon.footballv2.notification.domain.NotificationType;

public record MatchAcceptedPushEvent (
        Long receiverMemberId,
        Long teamMatchId,
        NotificationType type,
        String title,
        String body
){
}
