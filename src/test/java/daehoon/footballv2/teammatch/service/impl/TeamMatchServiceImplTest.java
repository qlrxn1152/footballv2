package daehoon.footballv2.teammatch.service.impl;

import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.team.domain.Team;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.exception.exceptions.NotFoundTeamException;
import daehoon.footballv2.team.exception.exceptions.NotSameTeamException;
import daehoon.footballv2.team.exception.exceptions.NotTeamLeaderException;
import daehoon.footballv2.team.repository.TeamRepository;
import daehoon.footballv2.team.service.TeamService;
import daehoon.footballv2.teammatch.domain.TeamMatch;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.response.TeamMatchCreateResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchPendingResponse;
import daehoon.footballv2.teammatch.exception.exceptions.DuplicateTeamMatchException;
import daehoon.footballv2.teammatch.repository.TeamMatchRepository;
import daehoon.footballv2.teammatch.service.TeamMatchService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeamMatchServiceImplTest {

    @Autowired private TeamMatchService teamMatchService;
    @Autowired private TeamMatchRepository teamMatchRepository;
    @Autowired private AuthService authService;
    @Autowired private TeamService teamService;
    @Autowired private TeamRepository teamRepository;

    @Test
    @DisplayName(value = "팀 매치 생성 성공")
    void teamMatchCreate() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        // when
        TeamMatchCreateResponse response = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId());

        // then
        assertThat(response.getTeamMatchId()).isNotNull();
        assertThat(response.getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getHomeTeamName()).isEqualTo("teamA");
        assertThat(response.getStatus()).isEqualTo(TeamMatchStatus.PENDING);
    }

    @Test
    @DisplayName(value = "팀장이 아닌 회원은 매치 등록 실패")
    void teamMatchCreate_notTeamLeader() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        SignupResponse memberB = authService.signup("testB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청
        teamService.acceptRequest(request.getTeamJoinRequestId(), team.getTeamId(), member.getMemberId()); // memberA -> memberB 가 teamA 에 신청한 요청 수락. -> memberB : teamA 회원.

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), memberB.getMemberId()))
                .isInstanceOf(NotTeamLeaderException.class)
                .hasMessage("팀장이 아닙니다.");
    }

    @Test
    @DisplayName(value = "다른 팀 팀장은 매치 등록 실패")
    void teamMatchCreate_notSameTeamOtherTeamLeader() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        SignupResponse memberB = authService.signup("testB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        teamService.createTeam("teamB", memberB.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), memberB.getMemberId()))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "존재하지 않는 팀이면 실패")
    void teamMatchCreate_notExistTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(9999L, member.getMemberId()))
                .isInstanceOf(NotFoundTeamException.class)
                .hasMessage("팀 조회 실패");
    }

    @Test
    @DisplayName(value = "존재하지 않는 회원이면 실패")
    void teamMatchCreate_notExistMember() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), 9999L))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

    @Test
    @DisplayName(value = "이미 진행중인 매치가 있으면 실패 _ PENDING")
    void teamMatchCreate_duplicateTeamMatch_pending() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId()); // 매치 등록

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId()))
                .isInstanceOf(DuplicateTeamMatchException.class)
                .hasMessage("이미 등록된 매치요청이 존재합니다.");
    }

    @Test
    @DisplayName(value = "이미 진행중인 매치가 있으면 실패 _ MATCHED")
    void teamMatchCreate_duplicateTeamMatch_matched() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        SignupResponse member2 = authService.signup("test2", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamCreateResponse team2 = teamService.createTeam("teamB", member2.getMemberId());

        TeamMatchCreateResponse response = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId());// 매치 등록
        TeamMatch teamMatch = teamMatchRepository.findById(response.getTeamMatchId()).get();

        Team teamB = teamRepository.findById(team2.getTeamId()).get();

        teamMatch.matchedTheMatch(teamB); // 기존 요청 매칭잡힘 -> MATCHED 로 변경.

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId()))
                .isInstanceOf(DuplicateTeamMatchException.class)
                .hasMessage("이미 진행중인 매치가 존재합니다.");
    }


    @Test
    @DisplayName(value = "PENDING 인 매치들 조회")
    void findPendingMatches() throws Exception {
        // given
        SignupResponse member = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId());

        // when
        List<TeamMatchPendingResponse> response = teamMatchService.findPendingTeamMatches();

        // then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getTeamMatchId()).isEqualTo(teamMatch.getTeamMatchId());
        assertThat(response.get(0).getHomeTeamId()).isEqualTo(teamMatch.getHomeTeamId());
        assertThat(response.get(0).getHomeTeamName()).isEqualTo("teamA");
        assertThat(response.get(0).getStatus()).isEqualTo(TeamMatchStatus.PENDING);
        assertThat(response.get(0).getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName(value = "MATCHED 매치는 조회안됨")
    void findPendingMatches_noContainStatusIsMatched() throws Exception {
        // given
        SignupResponse member = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());
        Team bTeam = teamRepository.findById(team.getTeamId()).get();

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId());
        TeamMatch aa = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();

        aa.matchedTheMatch(bTeam);

        // when
        List<TeamMatchPendingResponse> response = teamMatchService.findPendingTeamMatches();

        // then
        assertThat(response).hasSize(0);
    }

    @Test
    @DisplayName(value = "매치가 존재하지 않으면, 빈 리스트")
    void findPendingMatches_notExistMatch() throws Exception {
        // when
        List<TeamMatchPendingResponse> response = teamMatchService.findPendingTeamMatches();

        // then
        assertThat(response).hasSize(0);
    }

    @Test
    @DisplayName(value = "등록순으로 정렬( 최신순으로 조회 )")
    void findPendingMatches_orderByCreatedAtDesc() throws Exception {
        // given
        SignupResponse member = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId());
        TeamMatchCreateResponse teamMatchB = teamMatchService.createTeamMatch(teamB.getTeamId(), memberB.getMemberId());

        // when
        List<TeamMatchPendingResponse> response = teamMatchService.findPendingTeamMatches();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getTeamMatchId()).isEqualTo(teamMatchB.getTeamMatchId());
        assertThat(response.get(1).getTeamMatchId()).isEqualTo(teamMatch.getTeamMatchId());
    }







}