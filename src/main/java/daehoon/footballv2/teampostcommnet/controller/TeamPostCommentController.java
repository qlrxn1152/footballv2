package daehoon.footballv2.teampostcommnet.controller;

import daehoon.footballv2.security.jwt.LoginMember;
import daehoon.footballv2.teampostcommnet.dto.request.TeamPostCommentCreateRequest;
import daehoon.footballv2.teampostcommnet.dto.response.TeamPostCommentResponse;
import daehoon.footballv2.teampostcommnet.service.TeamPostCommentService;
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
public class TeamPostCommentController {

    private final TeamPostCommentService teamPostCommentService;

    @PostMapping("/api/teams/{teamId}/posts/{postId}/comments")
    public ResponseEntity<TeamPostCommentResponse> createTeamPostComment(@PathVariable Long teamId, @PathVariable Long postId, @Parameter(hidden = true) @LoginMember Long memberId, @Valid @RequestBody TeamPostCommentCreateRequest request) {
        TeamPostCommentResponse response = teamPostCommentService.createTeamPostComment(teamId, postId, memberId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/teams/{teamId}/posts/{postId}/comments")
    public ResponseEntity<List<TeamPostCommentResponse>> findTeamPostComments(@PathVariable Long teamId, @PathVariable Long postId, @Parameter(hidden = true) @LoginMember Long memberId) {
        List<TeamPostCommentResponse> response = teamPostCommentService.findTeamPostComments(teamId, postId, memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }



}
