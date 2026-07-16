package daehoon.footballv2.teampost.controller;

import daehoon.footballv2.security.jwt.LoginMember;
import daehoon.footballv2.teampost.dto.request.TeamPostCreateRequest;
import daehoon.footballv2.teampost.dto.response.TeamPostDetailResponse;
import daehoon.footballv2.teampost.dto.response.TeamPostSummaryResponse;
import daehoon.footballv2.teampost.service.TeamPostService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TeamPostController {

    private final TeamPostService teamPostService;


    @PostMapping("/api/teams/{teamId}/posts")
    public ResponseEntity<TeamPostDetailResponse> crateTeamPost(@Parameter(hidden = true) @LoginMember Long memberId, @PathVariable Long teamId, @RequestBody TeamPostCreateRequest request) {
        TeamPostDetailResponse response = teamPostService.createTeamPost(memberId, teamId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/teams/{teamId}/posts")
    public ResponseEntity<List<TeamPostSummaryResponse>> findTeamPosts(@Parameter(hidden = true) @LoginMember Long memberId, @PathVariable Long teamId) {
        List<TeamPostSummaryResponse> response = teamPostService.findTeamPosts(memberId, teamId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/api/teams/{teamId}/posts/{postId}")
    public ResponseEntity<TeamPostDetailResponse> findTeamPost(@Parameter(hidden = true) @LoginMember Long memberId, @PathVariable Long teamId, @PathVariable Long postId) {
        TeamPostDetailResponse response = teamPostService.findTeamPost(memberId, teamId, postId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
