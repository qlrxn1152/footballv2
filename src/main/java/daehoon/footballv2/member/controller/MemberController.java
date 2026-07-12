package daehoon.footballv2.member.controller;

import daehoon.footballv2.member.dto.response.*;
import daehoon.footballv2.member.service.MemberService;
import daehoon.footballv2.security.jwt.LoginMember;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.service.TeamService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final TeamService teamService;

    @GetMapping("/api/members/ranking")
    public ResponseEntity<List<MemberRankingResponse>> memberRanking() {
        List<MemberRankingResponse> response = memberService.membersRanking();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/members/{memberId}")
    public ResponseEntity<MemberDetailResponse> memberDetail(@PathVariable Long memberId) {
        MemberDetailResponse response = memberService.findMemberDetail(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/members/me")
    public ResponseEntity<MemberMeResponse> myPage(@Parameter(hidden = true) @LoginMember Long memberId) {
        MemberMeResponse response = memberService.findMyInfo(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/members/me/team-join-requests")
    public ResponseEntity<List<MyTeamJoinRequestResponse>> myRequests(@Parameter(hidden = true) @LoginMember Long memberId) {
        List<MyTeamJoinRequestResponse> response = memberService.findMyTeamJoinRequests(memberId, TeamJoinRequestStatus.PENDING);// PENDING 인 요청만 우선 ...

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/api/members/me/team-join-requests/{joinRequestId}/cancel")
    public ResponseEntity<MyTeamJoinRequestResponse> cancelRequest(@PathVariable Long joinRequestId, @Parameter(hidden = true) @LoginMember Long memberId) {
        // 해당 멤버의 가입신청들을 조회 -> 단, status = PENDING ..
        // joinRequestId 를 가진 TeamJoinRequest ... ( status = PENDING 이여야함. ) -> 요청 가지고옴 -> 요청 status = CANCELED 로 변경.
        MyTeamJoinRequestResponse response = memberService.cancelRequest(joinRequestId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 팀 탈퇴
    @DeleteMapping("/api/members/me/team")
    public ResponseEntity<TeamLeaveResponse> leaveTeam(@Parameter(hidden = true) @LoginMember Long memberId) {
        TeamLeaveResponse response = memberService.leaveTeam(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
