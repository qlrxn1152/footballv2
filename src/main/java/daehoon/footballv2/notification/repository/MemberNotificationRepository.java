package daehoon.footballv2.notification.repository;

import daehoon.footballv2.notification.domain.MemberNotification;
import daehoon.footballv2.notification.dto.response.MemberNotificationResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberNotificationRepository extends JpaRepository<MemberNotification, Long> {

    List<MemberNotification> findByReceiverMemberId(Long memberId);
}
