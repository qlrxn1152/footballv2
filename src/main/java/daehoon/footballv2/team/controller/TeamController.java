package daehoon.footballv2.team.controller;

import daehoon.footballv2.team.dto.request.teamcreate.TeamCreateRequest;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestDecisionResponse;
import daehoon.footballv2.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

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





}
