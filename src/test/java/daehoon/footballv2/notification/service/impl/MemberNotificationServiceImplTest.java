package daehoon.footballv2.notification.service.impl;

import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.notification.domain.NotificationType;
import daehoon.footballv2.notification.dto.response.MemberNotificationResponse;
import daehoon.footballv2.notification.service.MemberNotificationService;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.service.TeamService;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.response.TeamMatchAcceptResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchCreateResponse;
import daehoon.footballv2.teammatch.service.TeamMatchService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberNotificationServiceImplTest {

    @Autowired private TeamService teamService;
    @Autowired private MemberNotificationService memberNotificationService;
    @Autowired private AuthService authService;
    @Autowired private TeamMatchService teamMatchService;

    static LocalDateTime matchDate = LocalDateTime.of(2026, 7, 17, 13, 00, 00);

    @Test
    @DisplayName(value = "매치 수락 시 알림 생성")
    void acceptMatch_createNotification_success() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(), matchDate);

        // when
        TeamMatchAcceptResponse teamMatchResponse = teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());
        List<MemberNotificationResponse> notifications = memberNotificationService.findMyNotifications(memberA.getMemberId());

        // then
        assertThat(teamMatchResponse.getStatus()).isEqualTo(TeamMatchStatus.MATCHED);
        assertThat(notifications).hasSize(1);
        MemberNotificationResponse notification = notifications.get(0);
        assertThat(notification.getType()).isEqualTo(NotificationType.MATCH_ACCEPTED);
        assertThat(notification.getReferenceId()).isEqualTo(teamMatch.getTeamMatchId());
        assertThat(notification.isRead()).isFalse();
        assertThat(notification.getTitle()).isNotNull();
        assertThat(notification.getContent()).isNotNull();
    }




}