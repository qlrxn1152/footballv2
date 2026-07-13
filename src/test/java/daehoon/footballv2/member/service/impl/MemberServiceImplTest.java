package daehoon.footballv2.member.service.impl;

import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.member.dto.response.*;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.member.service.MemberService;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.domain.TeamRole;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.exception.exceptions.*;
import daehoon.footballv2.team.service.TeamService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceImplTest {

    @Autowired private MemberService memberService;
    @Autowired private AuthService authService;
    @Autowired private TeamService teamService;

    @Test
    @DisplayName(value = "랭킹순으로 멤버조회")
    void findRankingMembers() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        SignupResponse userB = authService.signup("userB", "1234");
        SignupResponse userC = authService.signup("userC", "1234");

        userA.setMemberRating(1800);
        userB.setMemberRating(1400);
        // rating -> userA, userB, userC => 1800, 1400, 1500

        // when
        List<MemberRankingResponse> members = memberService.membersRanking();

        // members = [userA, userC, userB]

        // then
        assertThat(members.size()).isEqualTo(3);
    }

    @Test
    @DisplayName(value = "팀이 있는 회원 상세페이지")
    void findMemberDetail() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        // when
        MemberDetailResponse response = memberService.findMemberDetail(member.getMemberId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("userA");
        assertThat(response.getMemberRating()).isEqualTo(1500);
        assertThat(response.getTotalGoalCount()).isZero();
        assertThat(response.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getTeamName()).isEqualTo("teamA");
        assertThat(response.getTeamRole()).isEqualTo(TeamRole.LEADER);
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getJoinedAt()).isNotNull();
    }

    @Test
    @DisplayName(value = "팀이 없는 회원 상세페이지")
    void findMemberDetailNoTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");

        // when
        MemberDetailResponse response = memberService.findMemberDetail(member.getMemberId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("userA");
        assertThat(response.getMemberRating()).isEqualTo(1500);
        assertThat(response.getTotalGoalCount()).isZero();
        assertThat(response.getTeamId()).isNull();
        assertThat(response.getTeamName()).isNull();
        assertThat(response.getTeamRole()).isNull();
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getJoinedAt()).isNull();
    }

    @Test
    @DisplayName(value = "존재하지 않는 memberId")
    void findMemberDetailNotExistMemberId() throws Exception {

        // when && then
        assertThatThrownBy(() -> memberService.findMemberDetail(9999L))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }


    @Test
    @DisplayName(value = "팀이 있는 회원 마이페이지")
    void findMyInfo() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        // when
        MemberMeResponse response = memberService.findMyInfo(member.getMemberId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("userA");
        assertThat(response.getMemberRating()).isEqualTo(1500);
        assertThat(response.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getTeamName()).isEqualTo("teamA");
        assertThat(response.getTeamRole()).isEqualTo(TeamRole.LEADER);
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getJoinedAt()).isNotNull();
    }

    @Test
    @DisplayName(value = "팀이 없는 회원 마이페이지")
    void findMyInfoNoTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");

        // when
        MemberMeResponse response = memberService.findMyInfo(member.getMemberId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("userA");
        assertThat(response.getMemberRating()).isEqualTo(1500);
        assertThat(response.getTeamId()).isNull();
        assertThat(response.getTeamName()).isNull();
        assertThat(response.getTeamRole()).isNull();
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getJoinedAt()).isNull();
    }

    @Test
    @DisplayName(value = "존재하지 않는 memberId_마이페이지")
    void findMyInfoNotExistMemberId() throws Exception {

        // when && then
        assertThatThrownBy(() -> memberService.findMyInfo(9999L))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

    @Test
    @DisplayName(value = "해당멤버가 팀 가입신청한 요청들 조회 ( PENDING 만 우선 조회. )")
    void findMyTeamJoinRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());// memberA -> teamA 생성.
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());

        SignupResponse memberC = authService.signup("userC", "1234");
        SignupResponse memberD = authService.signup("userD", "1234");


        teamService.joinRequest(team.getTeamId(), memberC.getMemberId()); // memberC -> teamA 에 가입신청.
        teamService.joinRequest(teamB.getTeamId(), memberC.getMemberId()); // memberC -> teamB 에 가입신청.

        // when
        List<MyTeamJoinRequestResponse> response = memberService.findMyTeamJoinRequests(memberC.getMemberId(), TeamJoinRequestStatus.PENDING);// teamA, teamB 에 대한 ..

        // then
        assertThat(response.size()).isEqualTo(2);
        assertThat(response).allMatch(request -> request.getStatus() == TeamJoinRequestStatus.PENDING);
    }

    @Test
    @DisplayName(value = "가입신청 취소")
    void cancelTeamJoinRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());//  memberB -> teamA 가입신청

        // when
        MyTeamJoinRequestResponse response = memberService.cancelRequest(request.getTeamJoinRequestId(), memberB.getMemberId());// memberB -> teamA 에 넣은 가입신청 취소

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("userB");
        assertThat(response.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getTeamName()).isEqualTo("teamA");
        assertThat(response.getStatus()).isEqualTo(TeamJoinRequestStatus.CANCELED);
    }

    @Test
    @DisplayName(value = "본인이 아닌 다른회원이 가입신청을 취소")
    void cancelTeamJoinRequest_fail_otherMember() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());//  memberB -> teamA 가입신청

        // when
        assertThatThrownBy(() -> memberService.cancelRequest(request.getTeamJoinRequestId(), memberC.getMemberId()))
                .isInstanceOf(TeamJoinRequestException.class)
                .hasMessage("회원의 요청이 아닙니다."); // memberC -> memberB가  teamA 에 넣은 가입신청 취소
    }

    @Test
    @DisplayName(value = "존재하지 않는 가입신청을 취소")
    void cancelTeamJoinRequest_fail_notExistJoinRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성

        // when
        assertThatThrownBy(() -> memberService.cancelRequest(999L, memberB.getMemberId()))
                .isInstanceOf(NotFoundTeamJoinRequestException.class)
                .hasMessage("가입신청 조회 실패"); // memberB -> 존재하지 않는 가입신청을 취소시도
    }

    @Test
    @DisplayName(value = "이미 승인된 요청을 취소")
    void cancelTeamJoinRequest_fail_alreadyAcceptRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 에 가입신청.
        teamService.acceptRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가 teamA 에 넣은 가입신청을 수락.

        // when
        assertThatThrownBy(() -> memberService.cancelRequest(request.getTeamJoinRequestId(), memberB.getMemberId()))
                .isInstanceOf(NotPendingException.class)
                .hasMessage("이미 승인 / 거절 된 요청입니다."); // memberB -> 이미 수락된 요청을 취소시도
    }

    @Test
    @DisplayName(value = "이미 거절된 요청을 취소")
    void cancelTeamJoinRequest_fail_alreadyRejectRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 에 가입신청.
        teamService.rejectRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가 teamA 에 넣은 가입신청을 거절.

        // when
        assertThatThrownBy(() -> memberService.cancelRequest(request.getTeamJoinRequestId(), memberB.getMemberId()))
                .isInstanceOf(NotPendingException.class)
                .hasMessage("이미 승인 / 거절 된 요청입니다."); // memberB -> 이미 취소된 요청을 취소시도
    }

    @Test
    @DisplayName(value = "이미 취소한 요청을 다시 취소시도")
    void cancelTeamJoinRequest_fail_alreadyCanceled() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 에 가입신청.
        memberService.cancelRequest(request.getTeamJoinRequestId(), memberB.getMemberId()); // memberB -> teamA 에 넣었던 가입신청을 취소.

        // when
        assertThatThrownBy(() -> memberService.cancelRequest(request.getTeamJoinRequestId(), memberB.getMemberId()))
                .isInstanceOf(NotPendingException.class)
                .hasMessage("이미 취소한 요청입니다."); // memberB -> 취소한 요청을 또 다시 취소시도
    }

    @Test
    @DisplayName(value = "팀 탈퇴 성공")
    void leaveTeam() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());// memberA -> teamA 팀 생성

        SignupResponse memberB = authService.signup("userB", "1234");
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청
        teamService.acceptRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); //memberA -> memberA 가 teamA 에 넣은 가입신청을 수락

        // when
        TeamLeaveResponse response = memberService.leaveTeam(memberB.getMemberId());// memberB -> teamA 팀 탈퇴.
        MemberMeResponse memberBInfo = memberService.findMyInfo(memberB.getMemberId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(memberB.getMemberId());
        assertThat(response.getUsername()).isEqualTo("userB");
        assertThat(response.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getTeamName()).isEqualTo("teamA");
        assertThat(response.getTeamRole()).isEqualTo(TeamRole.MEMBER);
        assertThat(response.isLeft()).isTrue();

        assertThat(memberBInfo.getUsername()).isEqualTo("userB");

        assertThat(memberBInfo.getTeamId()).isNull();
        assertThat(memberBInfo.getTeamRole()).isNull();
        assertThat(memberBInfo.getTeamName()).isNull();
        assertThat(memberBInfo.getJoinedAt()).isNull();
    }

    @Test
    @DisplayName(value = "팀장 탈퇴시도")
    void leaveTeam_teamLeader() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        teamService.createTeam("teamA", memberA.getMemberId());// memberA -> teamA 팀 생성

        // when && then
        assertThatThrownBy(() -> memberService.leaveTeam(memberA.getMemberId()))
                .isInstanceOf(CannotLeaveTeamLeaderException.class)
                .hasMessage("팀장은 탈퇴가 불가능합니다.");
    }

    @Test
    @DisplayName(value = "팀에 속하지 않은 회원이 탈퇴를 시도")
    void leaveTeam_notJoinedTeam() throws Exception {
        // given
        SignupResponse memberB = authService.signup("userB", "1234");

        // when && then
        assertThatThrownBy(() -> memberService.leaveTeam(memberB.getMemberId()))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("팀에 속해있지 않습니다.");
    }

    @Test
    @DisplayName(value = "존재하지 않는 memberId")
    void leaveTeam_notExistMemberId() throws Exception {
        // when && then
        assertThatThrownBy(() -> memberService.leaveTeam(999L))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패.");

    }








}
