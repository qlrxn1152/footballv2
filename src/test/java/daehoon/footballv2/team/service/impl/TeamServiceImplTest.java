package daehoon.footballv2.team.service.impl;

import daehoon.footballv2.auth.dto.response.signup.SignupResponse;
import daehoon.footballv2.auth.service.AuthService;
import daehoon.footballv2.member.exception.exceptions.NotFoundMemberException;
import daehoon.footballv2.team.domain.TeamJoinRequest;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.domain.TeamMember;
import daehoon.footballv2.team.domain.TeamRole;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamdetail.TeamDetailResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestDecisionResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestSummaryResponse;
import daehoon.footballv2.team.dto.response.teamleader.TeamLeaderTransferResponse;
import daehoon.footballv2.team.dto.response.teamlist.TeamSummaryResponse;
import daehoon.footballv2.team.dto.response.teammember.TeamMemberSummaryResponse;
import daehoon.footballv2.team.dto.response.teamname.TeamNameUpdateResponse;
import daehoon.footballv2.team.exception.exceptions.*;
import daehoon.footballv2.team.repository.TeamJoinRequestRepository;
import daehoon.footballv2.team.repository.TeamMemberRepository;
import daehoon.footballv2.team.service.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeamServiceImplTest {

    @Autowired AuthService authService;
    @Autowired TeamService teamService;
    @Autowired TeamMemberRepository teamMemberRepository;
    @Autowired TeamJoinRequestRepository teamJoinRequestRepository;


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




    // 가입신청
    @Test
    @DisplayName(value = "팀 가입신청")
    void createTeamRequestJoin() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());

        SignupResponse memberB = authService.signup("userB", "1234");

        // when
        TeamJoinRequestCreateResponse joinRequest = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> team 에 가입신청.

        TeamJoinRequest savedRequest = teamJoinRequestRepository.findById(joinRequest.getTeamJoinRequestId()).get();


        // then
        assertThat(joinRequest.getTeamJoinRequestId()).isNotNull();
        assertThat(joinRequest.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(joinRequest.getMemberId()).isEqualTo(memberB.getMemberId());
        assertThat(joinRequest.getStatus()).isEqualTo(TeamJoinRequestStatus.PENDING);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getStatus()).isEqualTo(TeamJoinRequestStatus.PENDING);
        assertThat(savedRequest.getTeam().getId()).isEqualTo(team.getTeamId());
        assertThat(savedRequest.getMember().getId()).isEqualTo(memberB.getMemberId());

        assertThat(joinRequest.getTeamName()).isEqualTo("teamA");
        assertThat(joinRequest.getUsername()).isEqualTo("userB");
    }

    @Test
    @DisplayName(value = "존재하지 않는 teamId")
    void teamJoinRequest_fail_notExistsTeamId() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");


        // when && then
        assertThatThrownBy(() -> teamService.joinRequest(999L, member.getMemberId()))
                .isInstanceOf(NotFoundTeamException.class)
                .hasMessage("팀 조회 실패");
    }

    @Test
    @DisplayName(value = "존재하지 않는 memberId")
    void teamJoinRequest_fail_notExistsmemberId() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());


        // when && then
        assertThatThrownBy(() -> teamService.joinRequest(team.getTeamId(), 999L))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

    @Test
    @DisplayName(value = "이미 팀에 속해있는 상태")
    void teamJoinRequest_fail_AlreadyJoinedTeam() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());


        // when && then
        assertThatThrownBy(() -> teamService.joinRequest(team.getTeamId(), member.getMemberId()))
                .isInstanceOf(AlreadyJoinedTeamException.class)
                .hasMessage("이미 팀에 소속된 회원입니다.");
    }

    @Test
    @DisplayName(value = "중복 가입요청")
    void teamJoinRequest_fail_DuplicateRequest() throws Exception {
        // given
        SignupResponse member = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", member.getMemberId());
        teamService.joinRequest(team.getTeamId(), memberB.getMemberId());


        // when && then
        assertThatThrownBy(() -> teamService.joinRequest(team.getTeamId(), memberB.getMemberId()))
                .isInstanceOf(DuplicateTeamJoinRequestException.class)
                .hasMessage("이미 가입신청한 팀입니다.");
    }


    // 가입신청 승인
    @Test
    @DisplayName(value = "가입신청 승인 성공")
    void acceptTeamJoinRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());// userA -> teamA 라는 팀 생성.

        TeamJoinRequestCreateResponse joinRequest = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 팀에 가입신청

        // when
        TeamJoinRequestDecisionResponse response = teamService.acceptRequest(joinRequest.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId());// mermberA -> memberB 가 넣은 가입신청을 수락
        TeamMember teamMemberB = teamMemberRepository.findByMemberId(memberB.getMemberId()).get();

        // then
        assertThat(teamMemberB.getTeamRole()).isEqualTo(TeamRole.MEMBER);
        assertThat(teamMemberB.getTeam().getId()).isEqualTo(team.getTeamId());
        assertThat(teamMemberB.getTeam().getTeamName()).isEqualTo(team.getTeamName());

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TeamJoinRequestStatus.ACCEPTED);
    }


    // 가입신청 거절
    @Test
    @DisplayName(value = "가입신청 거절 성공")
    void rejectTeamJoinRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());// userA -> teamA 라는 팀 생성.

        TeamJoinRequestCreateResponse joinRequest = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 팀에 가입신청

        // when
        TeamJoinRequestDecisionResponse response = teamService.rejectRequest(joinRequest.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId());// mermberA -> memberB 가 넣은 가입신청을 수락

        // then
        assertThat(teamMemberRepository.findByMemberId(memberB.getMemberId())).isEmpty();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TeamJoinRequestStatus.REJECTED);
    }

    @Test
    @DisplayName(value = "팀장이 아닌 회원이 요청들을 처리")
    void notTeamLeader() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> 팀 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청.
        teamService.acceptRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 의 가입신청 수락

        TeamJoinRequestCreateResponse request2 = teamService.joinRequest(team.getTeamId(), memberC.getMemberId());// memberC -> teamA 에 가입신청

        // when && then // 팀장이 아닌, memberB -> memberC 의 가입신청을 수락
        assertThatThrownBy(() -> teamService.acceptRequest(request2.getTeamJoinRequestId(), team.getTeamId(), memberB.getMemberId()))
                .isInstanceOf(NotTeamLeaderException.class)
                .hasMessage("팀장이 아닙니다.");

        // 팀장이 아닌, memberB -> memberC 의 가입신청을 거절
        assertThatThrownBy(() -> teamService.rejectRequest(request2.getTeamJoinRequestId(), team.getTeamId(), memberB.getMemberId()))
                .isInstanceOf(NotTeamLeaderException.class)
                .hasMessage("팀장이 아닙니다.");
    }

    @Test
    @DisplayName(value = "존재하지 않는 joinRequestId")
    void notExistJoinRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> 팀 생성

        // memberA 존재하지 않는 가입신청을 수락 / 거절
        // when && then
        assertThatThrownBy(() -> teamService.acceptRequest(999L, team.getTeamId(), memberB.getMemberId()))
                .isInstanceOf(NotFoundTeamJoinRequestException.class)
                .hasMessage("가입신청 조회 실패");


        assertThatThrownBy(() -> teamService.rejectRequest(999L, team.getTeamId(), memberB.getMemberId()))
                .isInstanceOf(NotFoundTeamJoinRequestException.class)
                .hasMessage("가입신청 조회 실패");
    }

    @Test
    @DisplayName(value = "다른팀의 요청을 수락 / 거절")
    void otherTeamJoinRequest() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> 팀 생성
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());// memberB -> 팀 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(teamB.getTeamId(), memberC.getMemberId());// memberC -> teamB 에 가입신청.

        // memberA 자기팀이 아닌, memberB 팀의 요청을 수락 / 거절
        // when && then
        assertThatThrownBy(() -> teamService.acceptRequest(request.getTeamJoinRequestId(), teamB.getTeamId(), memberA.getMemberId()))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("다른팀 소속입니다.");


        assertThatThrownBy(() -> teamService.rejectRequest(request.getTeamJoinRequestId(), teamB.getTeamId(), memberA.getMemberId()))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "이미 승인 된 요청을 또 다시 승인")
    void duplicateRequestAccept() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> 팀 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 에 가입신청.
        teamService.acceptRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가 신청한 가입신청을 승인.

        // memberA 이미 승인 된 요청을 또 다시 승인
        // when && then
        assertThatThrownBy(() -> teamService.acceptRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()))
                .isInstanceOf(TeamJoinRequestException.class)
                .hasMessage("이미 가입신청을 승인 / 거절한 요청입니다.");
    }

    @Test
    @DisplayName(value = "이미 승인 된 요청을 또 다시 거절")
    void duplicateRequestReject() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> 팀 생성
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 에 가입신청.
        teamService.rejectRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가 신청한 가입신청을 거절.

        // memberA 이미 거절 된 요청을 또 다시 승인
        // when && then
        assertThatThrownBy(() -> teamService.rejectRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()))
                .isInstanceOf(TeamJoinRequestException.class)
                .hasMessage("이미 가입신청을 승인 / 거절한 요청입니다.");
    }

    // 가입요청들 조회
    @Test
    @DisplayName(value = "status 에 따라 가입신청들 조회 ( PENDING )")
    void findJoinRequests_pending() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성

        teamService.joinRequest(team.getTeamId(), memberB.getMemberId()); // memberB -> teamA 가입신청.
        teamService.joinRequest(team.getTeamId(), memberC.getMemberId()); // memberB -> teamA 가입신청.

        // when
        List<TeamJoinRequestSummaryResponse> requests = teamService.findJoinRequests(team.getTeamId(), memberA.getMemberId(), TeamJoinRequestStatus.PENDING);

        // then
        assertThat(requests).hasSize(2);
    }



    // 가입요청들 조회
    @Test
    @DisplayName(value = "status 에 따라 가입신청들 조회 ( REJECTED )")
    void findJoinRequests_rejected() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성

        TeamJoinRequestCreateResponse request1 = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청.
        TeamJoinRequestCreateResponse request2 = teamService.joinRequest(team.getTeamId(), memberC.getMemberId());// memberB -> teamA 가입신청.

        teamService.rejectRequest(request1.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberB 가입신청 거절
        teamService.rejectRequest(request2.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberC 가입신청 거절

        // when
        List<TeamJoinRequestSummaryResponse> requests = teamService.findJoinRequests(team.getTeamId(), memberA.getMemberId(), TeamJoinRequestStatus.REJECTED);

        // then
        assertThat(requests).hasSize(2);
    }

    // 가입요청들 조회
    @Test
    @DisplayName(value = "status 에 따라 가입신청들 조회 ( ACCEPTED )")
    void findJoinRequests_accepted() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성

        TeamJoinRequestCreateResponse request1 = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청.
        TeamJoinRequestCreateResponse request2 = teamService.joinRequest(team.getTeamId(), memberC.getMemberId());// memberB -> teamA 가입신청.

        teamService.acceptRequest(request1.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberB 가입신청 승인
        teamService.acceptRequest(request2.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberC 가입신청 승인

        // when
        List<TeamJoinRequestSummaryResponse> requests = teamService.findJoinRequests(team.getTeamId(), memberA.getMemberId(), TeamJoinRequestStatus.ACCEPTED);

        // then
        assertThat(requests).hasSize(2);
    }

    // 가입요청들 조회
    @Test
    @DisplayName(value = "팀장이 아닌 팀에속하지 않은 회원이 가입신청들 조회")
    void findJoinRequests_noTeamLeader() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성

        TeamJoinRequestCreateResponse request1 = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청.
        TeamJoinRequestCreateResponse request2 = teamService.joinRequest(team.getTeamId(), memberC.getMemberId());// memberB -> teamA 가입신청.

        // when && then
        assertThatThrownBy(() -> teamService.findJoinRequests(team.getTeamId(), memberB.getMemberId(), TeamJoinRequestStatus.PENDING))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

    // 가입요청들 조회
    @Test
    @DisplayName(value = "다른팀 팀장이 가입신청들 조회")
    void findJoinRequests_otherTeamLeader() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성
        teamService.createTeam("teamB", memberB.getMemberId()); // memberB -> teamB 생성

        TeamJoinRequestCreateResponse request2 = teamService.joinRequest(team.getTeamId(), memberC.getMemberId());// memberC -> teamA 가입신청.

        // when && then
        assertThatThrownBy(() -> teamService.findJoinRequests(team.getTeamId(), memberB.getMemberId(), TeamJoinRequestStatus.PENDING))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    // 가입요청들 조회
    @Test
    @DisplayName(value = "다른팀 회원이 가입신청들 조회")
    void findJoinRequests_otherTeamMember() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");
        SignupResponse memberD = authService.signup("userD", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성
        TeamCreateResponse teamB = teamService.createTeam("teamB", memberB.getMemberId());// memberB -> teamB 생성

        TeamJoinRequestCreateResponse request = teamService.joinRequest(teamB.getTeamId(), memberD.getMemberId());// memberD -> teamB 에 가입신청
        teamService.joinRequest(team.getTeamId(), memberC.getMemberId());// memberC -> teamA 가입신청.

        teamService.acceptRequest(request.getTeamJoinRequestId(), teamB.getTeamId(), memberB.getMemberId()); // memberB -> memberD 가 신청한 가입신청을 승인.

        // when && then
        assertThatThrownBy(() -> teamService.findJoinRequests(team.getTeamId(), memberD.getMemberId(), TeamJoinRequestStatus.PENDING))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "팀 멤버 조회")
    void findTeamMembers() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");
        SignupResponse memberD = authService.signup("userD", "1234");

        TeamJoinRequestCreateResponse request1 = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청
        TeamJoinRequestCreateResponse request2 = teamService.joinRequest(team.getTeamId(), memberC.getMemberId());// memberC -> teamA 가입신청
        teamService.acceptRequest(request1.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가입신청 승인
        teamService.acceptRequest(request2.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가입신청 승인

        // when
        List<TeamMemberSummaryResponse> members = teamService.findTeamMembers(team.getTeamId());

        // then
        assertThat(members).hasSize(3);
        assertThat(members)
                .allMatch(teamMember -> teamMember.getTeamName().equals(team.getTeamName()));
    }


    // 팀 상세페이지
    @Test
    @DisplayName(value = "팀 상세페이지 조회")
    void teamDetail() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");
        SignupResponse memberD = authService.signup("userD", "1234");

        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId());

        TeamJoinRequestCreateResponse request1 = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청
        TeamJoinRequestCreateResponse request2 = teamService.joinRequest(team.getTeamId(), memberC.getMemberId());// memberC -> teamA 가입신청
        TeamJoinRequestCreateResponse request3 = teamService.joinRequest(team.getTeamId(), memberD.getMemberId());// memberD -> teamA 가입신청

        teamService.acceptRequest(request1.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가입신청 승인
        teamService.acceptRequest(request2.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가입신청 승인
        teamService.rejectRequest(request3.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가입신청 거절

        // when
        TeamDetailResponse response = teamService.findTeamDetail(team.getTeamId());// teamA 상세페이지

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTeamName()).isEqualTo("teamA");
        assertThat(response.getMemberCount()).isEqualTo(3);
        assertThat(response.getLeaderMemberId()).isEqualTo(memberA.getMemberId());
        assertThat(response.getLeaderUsername()).isEqualTo("userA");
    }

    @Test
    @DisplayName(value = "존재하지 않는 팀 상세페이지")
    void teamDetail_notExistTeam() throws Exception {

        // when && then
        assertThatThrownBy(() -> teamService.findTeamDetail(999L))
                .isInstanceOf(NotFoundTeamException.class)
                .hasMessage("팀 조회 실패");
    }

    // 팀 목록
    @Test
    @DisplayName(value = "팀 목록 조회")
    void findTeams() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        SignupResponse memberB = authService.signup("userB", "1234");
        SignupResponse memberC = authService.signup("userC", "1234");
        SignupResponse memberD = authService.signup("userD", "1234");

        TeamCreateResponse teamA = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성
        teamService.createTeam("teamB", memberB.getMemberId()); // memberB -> teamB 생성
        teamService.createTeam("teamC", memberC.getMemberId()); // memberC -> teamC 생성


        TeamJoinRequestCreateResponse request = teamService.joinRequest(teamA.getTeamId(), memberD.getMemberId());// memberD -> teamA 가입신청

        teamService.acceptRequest(request.getTeamJoinRequestId(), teamA.getTeamId(), memberA.getMemberId()); // memberA -> memberD 가입신청 승인

        // when
        List<TeamSummaryResponse> teams = teamService.findTeams();

        // then
        assertThat(teams).hasSize(3);
        assertThat(teams)
                .allMatch(team -> team.getTeamRating() == 1500);

        assertThat(teams.get(0).getTeamName()).isEqualTo("teamA");
        assertThat(teams.get(0).getLeaderMemberId()).isEqualTo(memberA.getMemberId());
        assertThat(teams.get(0).getMemberCount()).isEqualTo(2);
        assertThat(teams.get(0).getLeaderUsername()).isEqualTo("userA");
    }

    @Test
    @DisplayName(value = "팀장 위임 성공")
    void transferTeamLeader() throws Exception {
        // given
        SignupResponse memberA = authService.signup("userA", "1234");
        TeamCreateResponse team = teamService.createTeam("teamA", memberA.getMemberId()); // memberA -> teamA 생성

        SignupResponse memberB = authService.signup("userB", "1234");
        TeamJoinRequestCreateResponse request = teamService.joinRequest(team.getTeamId(), memberB.getMemberId());// memberB -> teamA 가입신청
        teamService.acceptRequest(request.getTeamJoinRequestId(), team.getTeamId(), memberA.getMemberId()); // memberA -> memberB 가 신청한 가입신청 승인

        TeamMember memberATeamMember = teamMemberRepository.findByMemberId(memberA.getMemberId()).get();
        TeamMember memberBTeamMember = teamMemberRepository.findByMemberId(memberB.getMemberId()).get();

        // when
        TeamLeaderTransferResponse response = teamService.transferLeader(team.getTeamId(), memberA.getMemberId(), memberB.getMemberId());// memberA -> memberB 로 팀장변경.

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTeamId()).isEqualTo(team.getTeamId());
        assertThat(response.getOldLeaderMemberId()).isEqualTo(memberA.getMemberId());
        assertThat(response.getNewLeaderMemberId()).isEqualTo(memberB.getMemberId());

        assertThat(memberATeamMember.getTeamRole()).isEqualTo(TeamRole.MEMBER);
        assertThat(memberBTeamMember.getTeamRole()).isEqualTo(TeamRole.LEADER);
    }

    @Test
    @DisplayName(value = "팀장이 아닌 회원이 팀장변경 시도 실패")
    void transferTeamLeader_notTeamLeader() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        SignupResponse userB = authService.signup("userB", "1234");
        SignupResponse userC = authService.signup("userC", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());
        TeamJoinRequestCreateResponse request = teamService.joinRequest(teamA.getTeamId(), userB.getMemberId());
        TeamJoinRequestCreateResponse request2 = teamService.joinRequest(teamA.getTeamId(), userC.getMemberId());
        teamService.acceptRequest(request.getTeamJoinRequestId(), teamA.getTeamId(), userA.getMemberId());
        teamService.acceptRequest(request2.getTeamJoinRequestId(), teamA.getTeamId(), userA.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.transferLeader(teamA.getTeamId(), userB.getMemberId(), userC.getMemberId()))
                .isInstanceOf(NotTeamLeaderException.class)
                .hasMessage("팀장이 아닙니다.");
    }

    @Test
    @DisplayName(value = "다른 팀 회원에게 팀장변경 시도 실패")
    void transferTeamLeader_notTeamJoinedMember1() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        SignupResponse userB = authService.signup("userB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());
        TeamCreateResponse teamB = teamService.createTeam("teamB", userB.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.transferLeader(teamA.getTeamId(), userA.getMemberId(), userB.getMemberId()))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("해당 팀의 멤버가 아닙니다.");
    }

    @Test
    @DisplayName(value = "팀에 속하지 않은 회원에게 팀장변경 시도 실패")
    void transferTeamLeader_notTeamJoinedMember2() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        SignupResponse userB = authService.signup("userB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.transferLeader(teamA.getTeamId(), userA.getMemberId(), userB.getMemberId()))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

    @Test
    @DisplayName(value = "자기 자신에게 팀장변경 시도 실패")
    void transferTeamLeader_sameMember() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.transferLeader(teamA.getTeamId(), userA.getMemberId(), userA.getMemberId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 회원으로는 변경이 불가능합니다.");
    }

    @Test
    @DisplayName(value = "존재하지 않는 팀")
    void transferTeamLeader_notTeam() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");

        // when && then
        assertThatThrownBy(() -> teamService.transferLeader(999L, userA.getMemberId(), userA.getMemberId()))
                .isInstanceOf(NotFoundTeamException.class)
                .hasMessage("팀 조회 실패");
    }

    @Test
    @DisplayName(value = "존재하지 않는 새 팀장 회원")
    void transferTeamLeader_notNewTeamLeader() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.transferLeader(teamA.getTeamId(), userA.getMemberId(), 999L))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

    @Test
    @DisplayName(value = "팀 이름 변경 성공")
    void changeTeamName() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());

        // when
        TeamNameUpdateResponse response = teamService.updateTeamName(teamA.getTeamId(), userA.getMemberId(), "teamB");
        TeamDetailResponse detailResponse = teamService.findTeamDetail(teamA.getTeamId());

        // then
        assertThat(response.getTeamId()).isEqualTo(teamA.getTeamId());
        assertThat(response.getTeamName()).isEqualTo("teamB");
        assertThat(response.getLeaderMemberId()).isEqualTo(userA.getMemberId());
        assertThat(response.getLeaderUsername()).isEqualTo("userA");

        assertThat(detailResponse.getTeamName()).isEqualTo("teamB");
    }

    @Test
    @DisplayName(value = "팀장이 아닌 회원이 팀이름 변경 시도")
    void changeTeamName_notTeamLeader() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        SignupResponse userB = authService.signup("userB", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());

        TeamJoinRequestCreateResponse request = teamService.joinRequest(teamA.getTeamId(), userB.getMemberId());
        teamService.acceptRequest(request.getTeamJoinRequestId(), teamA.getTeamId(), userA.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.updateTeamName(teamA.getTeamId(), userB.getMemberId(), "teamB"))
                .isInstanceOf(NotTeamLeaderException.class)
                .hasMessage("팀장이 아닙니다.");
    }

    @Test
    @DisplayName(value = "다른팀 팀장이 팀이름 변경 시도")
    void changeTeamName_otherTeamLeader() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        SignupResponse userB = authService.signup("userB", "1234");

        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());
        teamService.createTeam("teamB", userB.getMemberId());


        // when && then
        assertThatThrownBy(() -> teamService.updateTeamName(teamA.getTeamId(), userB.getMemberId(), "teamB"))
                .isInstanceOf(NotJoinedTeamException.class)
                .hasMessage("다른팀 소속입니다.");
    }

    @Test
    @DisplayName(value = "팀이름 중복")
    void changeTeamName_duplicateTeamName() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        SignupResponse userB = authService.signup("userB", "1234");

        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());
        teamService.createTeam("teamB", userB.getMemberId());


        // when && then
        assertThatThrownBy(() -> teamService.updateTeamName(teamA.getTeamId(), userA.getMemberId(), "teamB"))
                .isInstanceOf(DuplicateTeamNameException.class)
                .hasMessage("팀 이름 중복");
    }

    @Test
    @DisplayName(value = "존재하지 않는 팀")
    void changeTeamName_notExistTeam() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");

        // when && then
        assertThatThrownBy(() -> teamService.updateTeamName(999L, userA.getMemberId(), "teamB"))
                .isInstanceOf(NotFoundTeamException.class)
                .hasMessage("팀 조회 실패");
    }

    @Test
    @DisplayName(value = "존재하지 않는 회원")
    void changeTeamName_notExistMember() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.updateTeamName(teamA.getTeamId(), 999L, "teamB"))
                .isInstanceOf(NotFoundMemberException.class)
                .hasMessage("멤버 조회 실패");
    }

    @Test
    @DisplayName(value = "해당팀장이 같은이름으로 변경시도")
    void changeTeamName_duplicateTeamName2() throws Exception {
        // given
        SignupResponse userA = authService.signup("userA", "1234");
        TeamCreateResponse teamA = teamService.createTeam("teamA", userA.getMemberId());

        // when && then
        assertThatThrownBy(() -> teamService.updateTeamName(teamA.getTeamId(), userA.getMemberId(), "teamA"))
                .isInstanceOf(SameTeamNameException.class)
                .hasMessage("같은 팀이름으로 변경은 불가능합니다.");
    }














}