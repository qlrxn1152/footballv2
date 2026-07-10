package daehoon.footballv2.member.controller;

import daehoon.footballv2.member.dto.response.MemberDetailResponse;
import daehoon.footballv2.member.dto.response.MemberMeResponse;
import daehoon.footballv2.member.dto.response.MemberRankingResponse;
import daehoon.footballv2.member.dto.response.MyTeamJoinRequestResponse;
import daehoon.footballv2.member.service.MemberService;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<MemberMeResponse> myPage(@RequestHeader("X-MEMBER-ID") Long memberId) {
        MemberMeResponse response = memberService.findMyInfo(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/members/me/team-join-requests")
    public ResponseEntity<List<MyTeamJoinRequestResponse>> myRequests(@RequestHeader("X-MEMBER-ID") Long memberId) {
        List<MyTeamJoinRequestResponse> response = memberService.findMyTeamJoinRequests(memberId, TeamJoinRequestStatus.PENDING);// PENDING 인 요청만 우선 ...

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/members/mew/team-join-requests/{joinRequestId}/cancel")
    public ResponseEntity<MyTeamJoinRequestResponse> cancelRequest(@PathVariable Long joinRequestId, @RequestHeader("X-MEMBER-ID") Long memberId) {
        // 해당 멤버의 가입신청들을 조회 -> 단, status = PENDING ..
        // joinRequestId 를 가진 TeamJoinRequest ... ( status = PENDING 이여야함. ) -> 요청 가지고옴 -> 요청 status = CANCELED 로 변경.
        MyTeamJoinRequestResponse response = memberService.cancelRequest(joinRequestId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
