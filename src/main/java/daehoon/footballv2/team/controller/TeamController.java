package daehoon.footballv2.team.controller;

import daehoon.footballv2.team.dto.request.teamcreate.TeamCreateRequest;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
}
