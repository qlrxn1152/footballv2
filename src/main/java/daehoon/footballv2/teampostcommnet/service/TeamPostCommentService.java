package daehoon.footballv2.teampostcommnet.service;

import daehoon.footballv2.teampostcommnet.dto.request.TeamPostCommentCreateRequest;
import daehoon.footballv2.teampostcommnet.dto.request.TeamPostCommentUpdateRequest;
import daehoon.footballv2.teampostcommnet.dto.response.TeamPostCommentResponse;

import java.util.List;

public interface TeamPostCommentService {

    TeamPostCommentResponse createTeamPostComment(Long teamId, Long postId, Long memberId, TeamPostCommentCreateRequest request);

    List<TeamPostCommentResponse> findTeamPostComments(Long teamId, Long postId, Long memberId);

    TeamPostCommentResponse updateTeamPostComment(Long memberId, Long teamId, Long postId, Long commentId, TeamPostCommentUpdateRequest request);

    void deleteTeamPostComment(Long memberId, Long teamId, Long postId, Long commentId);
}
