package daehoon.footballv2.notification.dto.response;

import daehoon.footballv2.notification.domain.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class MemberNotificationResponse {

    private Long notificationId;

    private NotificationType type;

    private String title;
    private String content;

    private Long referenceId;

    private boolean read;

    private LocalDateTime createdAt;

    public MemberNotificationResponse(Long notificationId, NotificationType type, String title, String content, Long referenceId, boolean read, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.referenceId = referenceId;
        this.read = read;
        this.createdAt = createdAt;
    }
}
