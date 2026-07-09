package daehoon.footballv2.team.service.impl;

import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.member.exception.NotFoundMemberException;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.domain.TeamRole;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.exception.exceptions.AlreadyJoinedTeamException;
import daehoon.footballv2.team.exception.exceptions.DuplicateTeamNameException;
import daehoon.footballv2.team.repository.TeamMemberRepository;
import daehoon.footballv2.team.service.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeamServiceImplTest {

    @Autowired AuthService authService;
    @Autowired TeamService teamService;
    @Autowired TeamMemberRepository teamMemberRepository;


    @Test
    @DisplayName(value = "팀 생성 성공")
    void createTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");

        // when
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamMember teamMember = teamMemberRepository.findByMemberId(member.getMemberId()).get();

        // then
        assertThat(team).isNotNull();

        assertThat(team.getTeamId()).isNotNull();
        assertThat(team.getTeamName()).isEqualTo("teamA");
        assertThat(team.getTeamRating()).isEqualTo(1500);
        assertThat(team.getLeaderUsername()).isEqualTo("userA");
        assertThat(team.getLeaderMemberId()).isEqualTo(teamMember.getMember().getId());
        assertThat(team.getLeaderMemberId()).isEqualTo(member.getMemberId());

        assertThat(teamMember.getTeamRole()).isEqualTo(TeamRole.LEADER);
        assertThat(teamMember.getMember().getId()).isEqualTo(member.getMemberId());
        assertThat(teamMember.getTeam().getId()).isEqualTo(team.getTeamId());
    }

    @Test
    @DisplayName(value = "팀생성 실패_ 중복팀이름")
    void createTeam_fail_duplicateTeamName() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        teamService.createTeam("teamA", member.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.createTeam("teamA", memberB.getMemberId()))
                .isInstanceOf(DuplicateTeamNameException.class)
                .hasMessage("팀 이름 중복");
    }

    @Test
    @DisplayName(value = "팀생성 실패_ 이미 팀 존재")
    void createTeam_fail_AlreadyJoinedTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");

        teamService.createTeam("teamA", member.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.createTeam("teamB", member.getMemberId()))
                .isInstanceOf(AlreadyJoinedTeamException.class)
                .hasMessage("이미 팀에 소속된 회원입니다.");
    }

    @Test
    @DisplayName(value = "팀생성 실패_memberId 미존재")
    void createTeam_fail_NoMemberId() throws Exception {

        // when && then
        assertThatThrownBy(() -> teamService.createTeam("teamB", 999L))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

}