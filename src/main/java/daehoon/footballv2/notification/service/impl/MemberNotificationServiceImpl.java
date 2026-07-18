package daehoon.footballv2.notification.service.impl;

import daehoon.footballv2.devicenotification.event.MatchAcceptedPushEvent;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.member.repository.MemberRepository;
import daehoon.footballv2.notification.domain.MemberNotification;
import daehoon.footballv2.notification.domain.NotificationType;
import daehoon.footballv2.notification.dto.response.MemberNotificationResponse;
import daehoon.footballv2.notification.dto.response.UnreadNotificationCountResponse;
import daehoon.footballv2.notification.exception.exceptions.NotFoundNotificationException;
import daehoon.footballv2.notification.repository.MemberNotificationRepository;
import daehoon.footballv2.notification.service.MemberNotificationService;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.domain.TeamRole;
import daehoon.footballv2.team.repository.TeamMemberRepository;
import daehoon.footballv2.team.validator.TeamValidator;
import daehoon.footballv2.teammatch.domain.TeamMatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class MemberNotificationServiceImpl implements MemberNotificationService {

    private final TeamValidator teamValidator;

    private final TeamMemberRepository teamMemberRepository;
    private final MemberNotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public List<MemberNotificationResponse> findMyNotifications(Long memberId) {
        teamValidator.validateMemberExists(memberId);

        return notificationRepository.findByReceiverMemberId(memberId)
                .stream()
                .map(notification -> new MemberNotificationResponse(
                        notification.getId(),
                        notification.getType(),
                        notification.getTitle(),
                        notification.getContent(),
                        notification.getReferenceId(),
                        notification.isRead(),
                        notification.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public UnreadNotificationCountResponse findUnreadNotificationCount(Long memberId) {
        teamValidator.validateMemberExists(memberId);

        int count = 0;

        List<MemberNotificationResponse> notifications = findMyNotifications(memberId);// 내 알람들 쭉 나옴 -> read = false 인거 숫자 세주셈

        for (MemberNotificationResponse notification : notifications) {
            if (!notification.isRead()) {
                count += 1;
            }
        }

        return new UnreadNotificationCountResponse(count);
    }

    @Override
    public void createMatchAcceptedNotification(TeamMatch teamMatch) {
        // 홈팀리더 조회 -> 알림 생성 -> 저장
        TeamMember teamMember = teamMemberRepository.findLeaderMemberByTeamIdAndTeamRole(teamMatch.getHomeTeam().getId(), TeamRole.LEADER)
                .orElseThrow(() -> new NotFoundMemberException("팀장 조회 실패"));

        Member leaderMember = teamMember.getMember();

        String title = "매치가 성사됐습니다.";
        String content = "매치 성사 완료.";

        notificationRepository.save(new MemberNotification(leaderMember, NotificationType.MATCH_ACCEPTED, title, content, teamMatch.getId()));

        eventPublisher.publishEvent(new MatchAcceptedPushEvent(
                leaderMember.getId(),
                teamMatch.getId(),
                NotificationType.MATCH_ACCEPTED,
                title,
                content
        ));


    }

    @Override
    public void readNotification(Long memberId, Long notificationId) {
        teamValidator.validateMemberExists(memberId);

        MemberNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundNotificationException("알림 조회 실패"));

        notification.read();
    }


}
