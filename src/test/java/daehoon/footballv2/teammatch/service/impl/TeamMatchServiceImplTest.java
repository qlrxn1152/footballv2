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
import daehoon.footballv2.teammatch.dto.request.TeamMatchGoalCreateRequest;
import daehoon.footballv2.teammatch.dto.response.*;
import daehoon.footballv2.teammatch.exception.exceptions.*;
import daehoon.footballv2.teammatch.repository.TeamMatchRepository;
import daehoon.footballv2.teammatch.service.TeamMatchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeamMatchServiceImplTest {

    @Autowired private TeamMatchService teamMatchService;
    @Autowired private TeamMatchRepository teamMatchRepository;
    @Autowired private AuthService authService;
    @Autowired private TeamService teamService;
    @Autowired private TeamRepository teamRepository;

    List<TeamMatchGoalCreateRequest> testList = new ArrayList<>();

    @Test
    @DisplayName(value = "팀 매치 생성 성공")
    void teamMatchCreate() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        // when
        TeamMatchCreateResponse response = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId(), LocalDateTime.of(2026, 1,1,1,1));

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
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), memberB.getMemberId(), LocalDateTime.of(2026, 1,1,1,1)))
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
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), memberB.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "존재하지 않는 팀이면 실패")
    void teamMatchCreate_notExistTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(9999L, member.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)))
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
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), 9999L,  LocalDateTime.of(2026, 1,1,1,1)))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

    @Test
    @DisplayName(value = "이미 진행중인 매치가 있으면 실패 _ PENDING")
    void teamMatchCreate_duplicateTeamMatch_pending() throws Exception {
        // given
        SignupResponse member = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)); // 매치 등록

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)))
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

        TeamMatchCreateResponse response = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// 매치 등록
        TeamMatch teamMatch = teamMatchRepository.findById(response.getTeamMatchId()).get();

        Team teamB = teamRepository.findById(team2.getTeamId()).get();

        teamMatch.matchedTheMatch(teamB); // 기존 요청 매칭잡힘 -> MATCHED 로 변경.

        // when && then
        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)))
                .isInstanceOf(DuplicateTeamMatchException.class)
                .hasMessage("이미 진행중인 매치가 존재합니다.");

        assertThatThrownBy(() -> teamMatchService.createTeamMatch(team2.getTeamId(), member2.getMemberId(), LocalDateTime.of(2026, 1, 1, 1, 1)))
                .isInstanceOf(DuplicateTeamMatchException.class)
                .hasMessage("이미 진행중인 매치가 존재합니다.");
    }


    @Test
    @DisplayName(value = "PENDING 인 매치들 조회")
    void findPendingMatches() throws Exception {
        // given
        SignupResponse member = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches(TeamMatchStatus.PENDING);

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

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));
        TeamMatch aa = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();

        aa.matchedTheMatch(bTeam);

        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches(TeamMatchStatus.PENDING);

        // then
        assertThat(response).hasSize(0);
    }

    @Test
    @DisplayName(value = "매치가 존재하지 않으면, 빈 리스트")
    void findPendingMatches_notExistMatch() throws Exception {
        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches(TeamMatchStatus.PENDING);

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

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), member.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));
        TeamMatchCreateResponse teamMatchB = teamMatchService.createTeamMatch(teamB.getTeamId(), memberB.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches(TeamMatchStatus.PENDING);

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getTeamMatchId()).isEqualTo(teamMatchB.getTeamMatchId());
        assertThat(response.get(1).getTeamMatchId()).isEqualTo(teamMatch.getTeamMatchId());
    }

    @Test
    @DisplayName(value = "매치 수락 성공")
    void acceptTeamMatch() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        // when
        TeamMatchAcceptResponse response = teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        // then
        assertThat(response.getTeamMatchId()).isEqualTo(teamMatch.getTeamMatchId());
        assertThat(response.getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getAwayTeamId()).isEqualTo(teamB.getTeamId());
        assertThat(response.getStatus()).isEqualTo(TeamMatchStatus.MATCHED);
    }

    @Test
    @DisplayName(value = "존재하지 않는 매치 수락 실패")
    void acceptTeamMatch_notExistTeamMatch() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        // when && then
        assertThatThrownBy(() -> teamMatchService.acceptTeamMatch(999L, memberB.getMemberId()))
                .isInstanceOf(NotFoundTeamMatchException.class)
                .hasMessage("매치 조회 실패");
    }

    @Test
    @DisplayName(value = "팀장이 아닌 회원은 수락 실패")
    void acceptTeamMatch_notTeamLeader() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamMatchCreateResponse match = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        SignupResponse memberC = authService.signup("memberC", "1234");
        TeamJoinRequestCreateResponse request = teamService.joinRequest(teamB.getTeamId(), memberC.getMemberId());
        teamService.acceptRequest(request.getTeamJoinRequestId(), teamB.getTeamId(), memberB.getMemberId());


        // when && then
        assertThatThrownBy(() -> teamMatchService.acceptTeamMatch(match.getTeamMatchId(), memberC.getMemberId()))
                .isInstanceOf(NotTeamLeaderException.class)
                .hasMessage("팀장이 아닙니다.");
    }

    @Test
    @DisplayName(value = "자기 팀 매치는 수락 실패")
    void acceptTeamMatch_ownTeam() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamMatchCreateResponse match = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        // when && then
        assertThatThrownBy(() -> teamMatchService.acceptTeamMatch(match.getTeamMatchId(), memberA.getMemberId()))
                .isInstanceOf(CaannotAcceptOwnTeamMatchException.class)
                .hasMessage("자기팀이 신청한 매치에는 수락을 할 수 없습니다.");
    }

    @Test
    @DisplayName(value = "PENDING 이 아닌 매치에는 수락 실패")
    void acceptTeamMatch_notPending() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamMatchCreateResponse match = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        teamMatchService.acceptTeamMatch(match.getTeamMatchId(), memberB.getMemberId());

        SignupResponse memberC = authService.signup("memberC", "1234");
        teamService.createTeam("teamC", memberC.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamMatchService.acceptTeamMatch(match.getTeamMatchId(), memberC.getMemberId()))
                .isInstanceOf(NotPendingTeamMatchException.class)
                .hasMessage("PENDING 상태가 아닙니다.");
    }

    @Test
    @DisplayName(value = "이미 진행중인 매치가 있는경우 매치 수락 실패 ( PENDING 존재 )")
    void acceptTeamMatch_AlreadyExistTeamMatch() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse match = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)); // teamA -> 매치등록
        TeamMatchCreateResponse match2 = teamMatchService.createTeamMatch(teamB.getTeamId(), memberB.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)); // teamB -> 매칭등록.

        // when && then
        assertThatThrownBy(() -> teamMatchService.acceptTeamMatch(match.getTeamMatchId(), memberB.getMemberId())) // memberB -> teamA 에 매치수락 ( memberB, teamB 는 이미 진행중인 매치가 있으므로, 테스트에 실패해야한다.)
                .isInstanceOf(AlreadyExistTeamMatchException.class)
                .hasMessage("이미 진행중인 매치가 있습니다.");
    }

    @Test
    @DisplayName(value = "전체 매치 목록 조회 성공")
    void findMatches() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)); // teamA -> 매치등록
        teamMatchService.createTeamMatch(teamB.getTeamId(), memberB.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)); // teamB -> 매칭등록.

        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches();

        // then
        assertThat(response).hasSize(2);

    }

    @Test
    @DisplayName(value = "PENDING 매치 목록 조회 성공")
    void findMatches_pending() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)); // teamA -> 매치등록
        teamMatchService.createTeamMatch(teamB.getTeamId(), memberB.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1)); // teamB -> 매칭등록.

        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches(TeamMatchStatus.PENDING);

        // then
        assertThat(response).hasSize(2);
        assertThat(response).allMatch(match -> match.getAwayTeamId() == null);
        assertThat(response).allMatch(match -> match.getAwayTeamName() == null);
        assertThat(response).allMatch(match -> match.getAwayTeamRating() == null);
    }

    @Test
    @DisplayName(value = "MATCHED 매치 목록 조회 성공")
    void findMatches_matched() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA -> 매치등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches(TeamMatchStatus.MATCHED);

        // then
        assertThat(response).hasSize(1);

        assertThat(response).allMatch(match -> match.getAwayTeamId().equals(teamB.getTeamId()));
        assertThat(response).allMatch(match -> match.getHomeTeamId().equals(teamA.getTeamId()));
        assertThat(response).allMatch(match -> match.getStatus() == TeamMatchStatus.MATCHED);
    }

    @Test
    @DisplayName(value = "COMPLETED 매치 목록 조회 성공")
    void findMatches_completed() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA -> 매치등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        TeamMatchResultResponse result = teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 2, testList);

        TeamMatch tm = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();
        tm.completedMatch(result.getHomeScore(), result.getAwayScore());

        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches(TeamMatchStatus.COMPLETED); //

        // then
        assertThat(response).hasSize(1);

        assertThat(response).allMatch(match -> match.getAwayTeamId().equals(teamB.getTeamId()));
        assertThat(response).allMatch(match -> match.getHomeTeamId().equals(teamA.getTeamId()));
        assertThat(response).allMatch(match -> match.getStatus() == TeamMatchStatus.COMPLETED);
    }

    @Test
    @DisplayName(value = "매치 미존재")
    void findMatches_notExistMatch() throws Exception {

        // when
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches();

        // then
        assertThat(response).hasSize(0);
    }

    @Test
    @DisplayName(value = "매치 결과 등록 성공 - homeTeam 승리")
    void matchResult_home() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId()); // teamB 가 수락

        // when
        TeamMatchResultResponse result = teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 1, testList);
        TeamMatch realTeamMatch = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();

        // then
        assertThat(result.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(realTeamMatch.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(result.getWinnerTeamId()).isEqualTo(teamA.getTeamId());
        assertThat(result.getHomeScore()).isEqualTo(3);
        assertThat(result.getAwayScore()).isEqualTo(1);
    }

    @Test
    @DisplayName(value = "매치 결과 등록 성공 - awayTeam 승리")
    void matchResult_away() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId()); // teamB 가 수락

        // when
        TeamMatchResultResponse result = teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 5, testList);
        TeamMatch realTeamMatch = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();

        // then
        assertThat(result.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(realTeamMatch.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(result.getWinnerTeamId()).isEqualTo(teamB.getTeamId());
        assertThat(result.getHomeScore()).isEqualTo(3);
        assertThat(result.getAwayScore()).isEqualTo(5);
    }

    @Test
    @DisplayName(value = "매치 결과 등록 성공 - draw")
    void matchResult_draw() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId()); // teamB 가 수락

        // when
        TeamMatchResultResponse result = teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 3, testList);
        TeamMatch realTeamMatch = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();

        // then
        assertThat(result.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(realTeamMatch.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(result.getWinnerTeamId()).isNull();
        assertThat(result.getWinnerTeamName()).isNull();
        assertThat(result.getHomeScore()).isEqualTo(3);
        assertThat(result.getAwayScore()).isEqualTo(3);
    }

    @Test
    @DisplayName(value = "PENDING 매치에 결과입력은 실패해야함")
    void matchResult_PENDING() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록

        // when && then
        assertThatThrownBy(() -> teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 3, testList))
                .isInstanceOf(TeamMatchStatusException.class)
                .hasMessage("MATCHED 상태가 아닙니다.");
    }

    @Test
    @DisplayName(value = "일반회원이 결과입력 실패")
    void matchResult_member() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        SignupResponse memberC = authService.signup("memberC", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());
        TeamJoinRequestCreateResponse request = teamService.joinRequest(teamA.getTeamId(), memberC.getMemberId());
        teamService.acceptRequest(request.getTeamJoinRequestId(), teamA.getTeamId(),  memberA.getMemberId()); // memberC -> teamA

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberC.getMemberId(), 3, 3, testList))
                .isInstanceOf(NotTeamLeaderException.class)
                .hasMessage("팀장이 아닙니다.");
    }

    @Test
    @DisplayName(value = "점수는 음수이면 안된다")
    void matchResult_score_neagative() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), -3, 1, testList))
                .isInstanceOf(TeamMatchResultScoreException.class)
                .hasMessage("점수는 음수일 수 없습니다.");
    }

    @Test
    @DisplayName(value = "점수는 null이면 안된다")
    void matchResult_score_null() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), null, 1, testList))
                .isInstanceOf(TeamMatchResultScoreException.class)
                .hasMessage("점수는 null 일 수 없습니다.");
    }



    @Test
    @DisplayName(value = "매치 참여 팀이 아닌 다른 팀장은 실패")
    void matchResult_otherTeam() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        SignupResponse memberC = authService.signup("memberC", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamCreateResponse teamC = teamService.createTeam("teamC", memberC.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId()); //teamB 가 매치 수락 => teamA VS teamB

        // when && then
        assertThatThrownBy(() -> teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberC.getMemberId(), 3, 1, testList))
                .isInstanceOf(NotSameTeamException.class)
                .hasMessage("해당 매치에 참여한 팀이 아닙니다.");
    }

    @Test
    @DisplayName(value = "이미 결과가 등록된 매치에는 결과등록에 실패해야함")
    void matchResult_alreadyExistMatchResult() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(teamA.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));// teamA 매치 등록
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId()); // teamB 가 매치 수락
        teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 3,testList); // teamA 가 매치 결과 , testList등록

        // when && then
        assertThatThrownBy(() -> teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 5, 3, testList))
                .isInstanceOf(AlreadyExistMatchResultException.class)
                .hasMessage("이미 결과가 입력된 매치입니다.");
    }

    @Test
    @DisplayName(value = "특정 팀 PENDING 매치들 조회")
    void PENDING_MatchHistory() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        // when
        List<TeamMatchHistoryResponse> response = teamMatchService.findTeamMatchHistory(team.getTeamId(), TeamMatchStatus.PENDING);

        // then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.get(0).getStatus()).isEqualTo(TeamMatchStatus.PENDING);
        assertThat(response.get(0).getHomeScore()).isNull();
        assertThat(response.get(0).getAwayScore()).isNull();
    }

    @Test
    @DisplayName(value = "특정 팀 MATCHED 매치들 조회")
    void MATCHED_MatchHistory() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        // when
        List<TeamMatchHistoryResponse> responseA = teamMatchService.findTeamMatchHistory(team.getTeamId(), TeamMatchStatus.MATCHED);
        List<TeamMatchHistoryResponse> responseB = teamMatchService.findTeamMatchHistory(teamB.getTeamId(), TeamMatchStatus.MATCHED);

        // then
        assertThat(responseA).hasSize(1);
        assertThat(responseA.get(0).getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(responseA.get(0).getStatus()).isEqualTo(TeamMatchStatus.MATCHED);
        assertThat(responseA.get(0).getHomeScore()).isNull();
        assertThat(responseA.get(0).getAwayScore()).isNull();

        assertThat(responseB).hasSize(1);
        assertThat(responseB.get(0).getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(responseB.get(0).getStatus()).isEqualTo(TeamMatchStatus.MATCHED);
        assertThat(responseB.get(0).getHomeScore()).isNull();
        assertThat(responseB.get(0).getAwayScore()).isNull();
    }

    @Test
    @DisplayName(value = "특정 팀 COMPLETED 매치들 조회")
    void COMPLETED_MatchHistory() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());
        teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 1, testList);

        // when
        List<TeamMatchHistoryResponse> response = teamMatchService.findTeamMatchHistory(team.getTeamId(), TeamMatchStatus.COMPLETED);

        // then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);

        assertThat(response.get(0).getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.get(0).getHomeScore()).isEqualTo(3);
        assertThat(response.get(0).getAwayScore()).isEqualTo(1);
        assertThat(response.get(0).getWinnerTeamId()).isEqualTo(team.getTeamId());
    }

    @Test
    @DisplayName(value = "해당 팀이 참여하지 않은 매치는 조회되지 않음")
    void Not_participate_MatchHistory() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        SignupResponse memberC = authService.signup("test", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());
        TeamCreateResponse teamC = teamService.createTeam("teamC", memberC.getMemberId()); // 매치가존재 ㄴㄴ

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        // when
        List<TeamMatchHistoryResponse> response = teamMatchService.findTeamMatchHistory(teamC.getTeamId(), TeamMatchStatus.PENDING);

        // then
        assertThat(response).isEmpty();
    }


    @Test
    @DisplayName(value = "존재하지 않는 팀이면 실패")
    void not_exist_team_MatchHistory() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));

        // when && then
        assertThatThrownBy(() -> teamMatchService.findTeamMatchHistory(999L, TeamMatchStatus.PENDING))
                .isInstanceOf(NotFoundTeamException.class)
                .hasMessage("팀 조회 실패");
    }

    @Test
    @DisplayName(value = "홈팀 승리 시 레이팅 반영")
    void home_rating() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());
        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        TeamMatchResultResponse result = teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 1, testList);// homeTeam, testList 승

        // when
        Team teamAEntity = teamRepository.findById(team.getTeamId()).get();
        Team teamBEntity = teamRepository.findById(teamB.getTeamId()).get();
        TeamMatch teamMatchEntity = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();


        // then
        assertThat(teamAEntity.getTeamRating()).isEqualTo(1530);
        assertThat(teamBEntity.getTeamRating()).isEqualTo(1470);
        assertThat(teamMatchEntity.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(result.getWinnerTeamId()).isEqualTo(team.getTeamId());
    }

    @Test
    @DisplayName(value = "어웨이팀 승리 시 레이팅 반영")
    void away_rating() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());
        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        TeamMatchResultResponse result = teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 5, testList);// awayTeam, testList 승

        // when
        Team teamAEntity = teamRepository.findById(team.getTeamId()).get();
        Team teamBEntity = teamRepository.findById(teamB.getTeamId()).get();
        TeamMatch teamMatchEntity = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();


        // then
        assertThat(teamBEntity.getTeamRating()).isEqualTo(1530);
        assertThat(teamAEntity.getTeamRating()).isEqualTo(1470);
        assertThat(teamMatchEntity.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(result.getWinnerTeamId()).isEqualTo(teamB.getTeamId());
    }

    @Test
    @DisplayName(value = "무승부")
    void draw_rating() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());
        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(),  LocalDateTime.of(2026, 1,1,1,1));
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        TeamMatchResultResponse result = teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 3, testList);// 무, testList승부

        // when
        Team teamAEntity = teamRepository.findById(team.getTeamId()).get();
        Team teamBEntity = teamRepository.findById(teamB.getTeamId()).get();
        TeamMatch teamMatchEntity = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();


        // then
        assertThat(teamBEntity.getTeamRating()).isEqualTo(1510);
        assertThat(teamAEntity.getTeamRating()).isEqualTo(1510);
        assertThat(teamMatchEntity.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(result.getWinnerTeamId()).isNull();
    }

    @Test
    @DisplayName(value = "중복 결과 등록 시, 점수반영 안됨")
    void already_exist_matchResult_rating() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());
        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(), LocalDateTime.of(2026,1,1,1,1)); // 2026-1-1 01:01에 매치
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());

        TeamMatchResultResponse result = teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 1, testList);// 홈팀 , testList승리

        // when

        // 이미 결과가 등록된매치에 또 매치결과 등록
        assertThatThrownBy(() -> teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 2, testList))
                .isInstanceOf(AlreadyExistMatchResultException.class)
                .hasMessage("이미 결과가 입력된 매치입니다.");

        Team teamAEntity = teamRepository.findById(team.getTeamId()).get();
        Team teamBEntity = teamRepository.findById(teamB.getTeamId()).get();
        TeamMatch teamMatchEntity = teamMatchRepository.findById(teamMatch.getTeamMatchId()).get();


        // then
        assertThat(teamAEntity.getTeamRating()).isEqualTo(1530);
        assertThat(teamBEntity.getTeamRating()).isEqualTo(1470);
        assertThat(teamMatchEntity.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(result.getWinnerTeamId()).isEqualTo(team.getTeamId());
        assertThat(teamMatch.getPlayedAt()).isEqualTo(LocalDateTime.of(2026,1,1,1,1));
        assertThat(teamMatchEntity.getPlayedAt()).isEqualTo(LocalDateTime.of(2026,1,1,1,1));
    }

    @Test
    @DisplayName(value = "PENDING 매치 상세 조회 성공")
    void matchDetail_pending() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(), LocalDateTime.of(2026, 1, 1, 1, 1));// 2026-1-1, 01:01 에 매치

        // when
        TeamMatchDetailResponse response = teamMatchService.findTeamMatchDetail(teamMatch.getTeamMatchId());

        // then
        assertThat(response.getStatus()).isEqualTo(TeamMatchStatus.PENDING);
        assertThat(response.getHomeTeamId()).isEqualTo(team.getTeamId());

        assertThat(response.getHomeScore()).isNull();
        assertThat(response.getAwayTeamId()).isNull();
        assertThat(response.getWinnerTeamId()).isNull();

        assertThat(response.getPlayedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 1, 1));
    }

    @Test
    @DisplayName(value = "MATCHED 매치 상세 조회 성공")
    void matchDetail_matched() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(), LocalDateTime.of(2026, 1, 1, 1, 1));// 2026-1-1, 01:01 에 매치
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());


        // when
        TeamMatchDetailResponse response = teamMatchService.findTeamMatchDetail(teamMatch.getTeamMatchId());

        // then
        assertThat(response.getStatus()).isEqualTo(TeamMatchStatus.MATCHED);
        assertThat(response.getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getAwayTeamId()).isEqualTo(teamB.getTeamId());

        assertThat(response.getHomeScore()).isNull();
        assertThat(response.getWinnerTeamId()).isNull();

        assertThat(response.getPlayedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 1, 1));
    }

    @Test
    @DisplayName(value = "COMPLETED 매치 상세 조회 성공")
    void matchDetail_completed() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(), LocalDateTime.of(2026, 1, 1, 1, 1));// 2026-1-1, 01:01 에 매치
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());
        teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 1, testList);

        // when
        TeamMatchDetailResponse response = teamMatchService.findTeamMatchDetail(teamMatch.getTeamMatchId());

        // then
        assertThat(response.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(response.getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getAwayTeamId()).isEqualTo(teamB.getTeamId());

        assertThat(response.getHomeScore()).isEqualTo(3);
        assertThat(response.getAwayScore()).isEqualTo(1);
        assertThat(response.getWinnerTeamId()).isEqualTo(team.getTeamId());

        assertThat(response.getHomeTeamRating()).isEqualTo(1530);
        assertThat(response.getAwayTeamRating()).isEqualTo(1470);

        assertThat(response.getPlayedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 1, 1));
    }

    @Test
    @DisplayName(value = "COMPLETED 매치 상세 조회 성공 _ 무승부")
    void matchDetail_completed_draw() throws Exception {
        // given
        SignupResponse memberA = authService.signup("memberA", "1234");
        SignupResponse memberB = authService.signup("memberB", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        TeamMatchCreateResponse teamMatch = teamMatchService.createTeamMatch(team.getTeamId(), memberA.getMemberId(), LocalDateTime.of(2026, 1, 1, 1, 1));// 2026-1-1, 01:01 에 매치
        teamMatchService.acceptTeamMatch(teamMatch.getTeamMatchId(), memberB.getMemberId());
        teamMatchService.registerMatchResult(teamMatch.getTeamMatchId(), memberA.getMemberId(), 3, 3, testList);

        // when
        TeamMatchDetailResponse response = teamMatchService.findTeamMatchDetail(teamMatch.getTeamMatchId());

        // then
        assertThat(response.getStatus()).isEqualTo(TeamMatchStatus.COMPLETED);
        assertThat(response.getHomeTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getAwayTeamId()).isEqualTo(teamB.getTeamId());

        assertThat(response.getHomeScore()).isEqualTo(3);
        assertThat(response.getAwayScore()).isEqualTo(3);
        assertThat(response.getWinnerTeamId()).isNull();

        assertThat(response.getHomeTeamRating()).isEqualTo(1510);
        assertThat(response.getAwayTeamRating()).isEqualTo(1510);

        assertThat(response.getPlayedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 1, 1));
    }

    @Test
    @DisplayName(value = "존재하지 않는 매치 조회 실패")
    void matchDetail_not_exist_match() throws Exception {
        // when && then
        assertThatThrownBy(() -> teamMatchService.findTeamMatchDetail(999L))
                .isInstanceOf(NotFoundTeamMatchException.class)
                .hasMessage("매치 조회 실패");

    }









}
