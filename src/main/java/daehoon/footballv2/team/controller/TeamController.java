package daehoon.footballv2.team.controller;

import daehoon.footballv2.security.jwt.LoginMember;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.dto.request.teamcreate.TeamCreateRequest;
import daehoon.footballv2.team.dto.request.teamleader.TeamLeaderTransferRequest;
import daehoon.footballv2.team.dto.request.teamname.TeamNameUpdateRequest;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamdetail.TeamDetailResponse;
import daehoon.footballv2.team.dto.response.teamdisband.TeamDisbandResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestDecisionResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestSummaryResponse;
import daehoon.footballv2.team.dto.response.teamleader.TeamLeaderTransferResponse;
import daehoon.footballv2.team.dto.response.teamlist.TeamSummaryResponse;
import daehoon.footballv2.team.dto.response.teammember.TeamMemberSummaryResponse;
import daehoon.footballv2.team.dto.response.teamname.TeamNameUpdateResponse;
import daehoon.footballv2.team.service.TeamService;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.response.TeamMatchHistoryResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchSummaryResponse;
import daehoon.footballv2.teammatch.service.TeamMatchService;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final TeamMatchService teamMatchService;

    // 팀 생성
    @PostMapping("/api/teams")
    public ResponseEntity<TeamCreateResponse> createTeam(@Valid @RequestBody TeamCreateRequest teamCreateRequest, @Parameter(hidden = true) @LoginMember Long memberId) {
        TeamCreateResponse response = teamService.createTeam(teamCreateRequest.getTeamName(), memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/teams/{teamId}/join-requests")
    public ResponseEntity<TeamJoinRequestCreateResponse> joinRequestTeam(@PathVariable Long teamId, @Parameter(hidden = true) @LoginMember Long memberId) {
        TeamJoinRequestCreateResponse response = teamService.joinRequest(teamId, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/teams/{teamId}/join-requests/{joinRequestId}/accept")
    public ResponseEntity<TeamJoinRequestDecisionResponse> acceptRequest(
            @PathVariable Long teamId,
            @PathVariable Long joinRequestId,
            @Parameter(hidden = true) @LoginMember Long memberId) {
        TeamJoinRequestDecisionResponse response = teamService.acceptRequest(joinRequestId, teamId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/api/teams/{teamId}/join-requests/{joinRequestId}/reject")
    public ResponseEntity<TeamJoinRequestDecisionResponse> rejectRequest(
            @PathVariable Long teamId,
            @PathVariable Long joinRequestId,
            @Parameter(hidden = true) @LoginMember Long memberId) {
        TeamJoinRequestDecisionResponse response = teamService.rejectRequest(joinRequestId, teamId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/teams/{teamId}/join-requests")
    public ResponseEntity<List<TeamJoinRequestSummaryResponse>> requests(
            @PathVariable Long teamId,
            @Parameter(hidden = true) @LoginMember Long memberId,
            @RequestParam TeamJoinRequestStatus status) {

        List<TeamJoinRequestSummaryResponse> response = teamService.findJoinRequests(teamId, memberId, status); // status 에 따라서 요청

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

    // 팀장 위임
    @PatchMapping("/api/teams/{teamId}/leader")
    public ResponseEntity<TeamLeaderTransferResponse> teamLeaderTransfer(@PathVariable Long teamId, @RequestHeader("X-MEMBER-ID") Long currentLeaderMemberId, @RequestBody TeamLeaderTransferRequest request) {
        TeamLeaderTransferResponse response = teamService.transferLeader(teamId, currentLeaderMemberId, request.getNewLeaderMemberId());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/api/teams/{teamId}/name")
    public ResponseEntity<TeamNameUpdateResponse> changeTeamName(@PathVariable Long teamId, @Parameter(hidden = true) @LoginMember Long memberId, @Valid @RequestBody TeamNameUpdateRequest request) {
        // memberId -> loginMemberId
        TeamNameUpdateResponse response = teamService.updateTeamName(teamId, memberId, request.getTeamName());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/api/teams/{teamId}")
    public ResponseEntity<TeamDisbandResponse> disbandTeam(@PathVariable Long teamId, @Parameter(hidden = true) @LoginMember Long memberId) {
        // memberId -> loginMemberId

        TeamDisbandResponse response = teamService.disbandTeam(teamId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/teams/{teamId}/matches")
    public ResponseEntity<List<TeamMatchHistoryResponse>> teamMatches(@PathVariable Long teamId, @RequestParam TeamMatchStatus status) {
        // 해당팀이 참여한 매치들을 조회할 수 있음 -> 전체공개. // 해당팀이 참여한 매치들 -> 매칭중, 매칭됨, 경기종료 .. 나눠서?

        List<TeamMatchHistoryResponse> response = teamMatchService.findTeamMatchHistory(teamId, status);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
