package daehoon.footballv2.devicenotification.dto.request;

import daehoon.footballv2.notification.domain.NotificationType;

public record FcmPushRequest (
        String title,
        String body,
        NotificationType type,
        Long referenceId
){}
