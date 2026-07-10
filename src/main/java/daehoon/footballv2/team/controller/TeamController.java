package daehoon.footballv2.team.controller;

import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.dto.request.teamcreate.TeamCreateRequest;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamdetail.TeamDetailResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestDecisionResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestSummaryResponse;
import daehoon.footballv2.team.dto.response.teamlist.TeamSummaryResponse;
import daehoon.footballv2.team.dto.response.teammember.TeamMemberSummaryResponse;
import daehoon.footballv2.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // 팀 생성
    @PostMapping("/api/teams")
    public ResponseEntity<TeamCreateResponse> createTeam(@Valid @RequestBody TeamCreateRequest teamCreateRequest, @RequestHeader("X-MEMBER-ID") Long memberId) {
        TeamCreateResponse response = teamService.createTeam(teamCreateRequest.getTeamName(), memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/teams/{teamId}/join-requests")
    public ResponseEntity<TeamJoinRequestCreateResponse> joinRequestTeam(@PathVariable Long teamId, @RequestHeader("X-MEMBER-ID") Long memberId) {
        TeamJoinRequestCreateResponse response = teamService.joinRequest(teamId, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/teams/{teamId}/join-requests/{joinRequestId}/accept")
    public ResponseEntity<TeamJoinRequestDecisionResponse> acceptRequest(
            @PathVariable Long teamId,
            @PathVariable Long joinRequestId,
            @RequestHeader("X-MEMBER-ID") Long loginMemberId) {
        TeamJoinRequestDecisionResponse response = teamService.acceptRequest(joinRequestId, teamId, loginMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/api/teams/{teamId}/join-requests/{joinRequestId}/reject")
    public ResponseEntity<TeamJoinRequestDecisionResponse> rejectRequest(
            @PathVariable Long teamId,
            @PathVariable Long joinRequestId,
            @RequestHeader("X-MEMBER-ID") Long loginMemberId) {
        TeamJoinRequestDecisionResponse response = teamService.rejectRequest(joinRequestId, teamId, loginMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/teams/{teamId}/join-requests")
    public ResponseEntity<List<TeamJoinRequestSummaryResponse>> requests(
            @PathVariable Long teamId,
            @RequestHeader("X-MEMBER-ID") Long leaderMemberId,
            @RequestParam TeamJoinRequestStatus status) {

        List<TeamJoinRequestSummaryResponse> response = teamService.findJoinRequests(teamId, leaderMemberId, status); // status 에 따라서 요청

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/teams/{teamId}/members")
    public ResponseEntity<List<TeamMemberSummaryResponse>> teamMembers(@PathVariable Long teamId) {
        List<TeamMemberSummaryResponse> response = teamService.findTeamMembers(teamId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/teams/{teamId}")
    public ResponseEntity<TeamDetailResponse> teamDetail(@PathVariable Long teamId) {
        TeamDetailResponse response = teamService.findTeamDetail(teamId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 팀 목록
    @GetMapping("/api/teams")
    public ResponseEntity<List<TeamSummaryResponse>> teams() {
        List<TeamSummaryResponse> response = teamService.findTeams();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
