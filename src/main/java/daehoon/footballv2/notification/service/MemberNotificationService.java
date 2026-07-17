package daehoon.footballv2.notification.service;

import daehoon.footballv2.notification.dto.response.MemberNotificationResponse;
import daehoon.footballv2.notification.dto.response.UnreadNotificationCountResponse;
import daehoon.footballv2.teammatch.domain.TeamMatch;

import java.util.List;

public interface MemberNotificationService {

    List<MemberNotificationResponse> findMyNotifications(Long memberId);

    UnreadNotificationCountResponse findUnreadNotificationCount(Long memberId);

    void createMatchAcceptedNotification(TeamMatch teamMatch);

    void readNotification(Long memberId, Long notificationId);


}
