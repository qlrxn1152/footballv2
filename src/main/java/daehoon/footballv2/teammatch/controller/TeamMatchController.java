package daehoon.footballv2.teammatch.controller;

import daehoon.footballv2.security.jwt.LoginMember;
import daehoon.footballv2.team.service.TeamService;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.request.TeamMatchCreateRequest;
import daehoon.footballv2.teammatch.dto.request.TeamMatchResultCreateRequest;
import daehoon.footballv2.teammatch.dto.response.TeamMatchAcceptResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchCreateResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchResultResponse;
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
public class TeamMatchController {

    private final TeamMatchService teamMatchService;
    private final TeamService teamService;

    // 매치생성
    @PostMapping("/api/teams/{teamId}/matches")
    public ResponseEntity<TeamMatchCreateResponse> matchCreate(@PathVariable Long teamId, @Parameter(hidden = true) @LoginMember Long memberId, @Valid @RequestBody TeamMatchCreateRequest request) {
        TeamMatchCreateResponse response = teamMatchService.createTeamMatch(teamId, memberId, request.getPlayedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/api/team-matches/{teamMatchId}/accept")
    public ResponseEntity<TeamMatchAcceptResponse> acceptTeamMatch(@PathVariable Long teamMatchId, @Parameter(hidden = true) @LoginMember Long memberId) {
        // memberId -> 어웨이팀 팀장, teamMatchId -> 어떤매치에 신청

        TeamMatchAcceptResponse response = teamMatchService.acceptTeamMatch(teamMatchId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/team-matches/pending")
    public ResponseEntity<List<TeamMatchSummaryResponse>> pendingTeamMatches() {
        // status = PENDING 인 매치들 가져와서 매치들 목록을 보여줌. // 즉, 팀장이 어떤 매치에 요청을 보낼수있나 보여줄수있는 기능.
        List<TeamMatchSummaryResponse> response = teamMatchService.findTeamMatches(TeamMatchStatus.PENDING);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/team-matches")
    public ResponseEntity<List<TeamMatchSummaryResponse>> findTeamMatches(@RequestParam(name = "status", required = false) TeamMatchStatus status) {

        if (status == null) {
            return  ResponseEntity.status(HttpStatus.OK).body(teamMatchService.findTeamMatches());
        }

        return ResponseEntity.status(HttpStatus.OK).body(teamMatchService.findTeamMatches(status));
    }

    @PostMapping("/api/team-matches/{teamMatchId}/result") // 매치 결과 입력
    public ResponseEntity<TeamMatchResultResponse> matchResult(@PathVariable Long teamMatchId, @Parameter(hidden = true) @LoginMember Long memberId, @Valid @RequestBody TeamMatchResultCreateRequest request) {
        // memberId -> 홈팀의 팀장이여야함.
        TeamMatchResultResponse response = teamMatchService.registerMatchResult(teamMatchId, memberId, request.getHomeScore(), request.getAwayScore());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
