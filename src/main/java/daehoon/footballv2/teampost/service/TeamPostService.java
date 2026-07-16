package daehoon.footballv2.teampost.service;

import daehoon.footballv2.teampost.dto.request.TeamPostCreateRequest;
import daehoon.footballv2.teampost.dto.request.TeamPostUpdateRequest;
import daehoon.footballv2.teampost.dto.response.TeamPostDetailResponse;
import daehoon.footballv2.teampost.dto.response.TeamPostSummaryResponse;

import java.util.List;

public interface TeamPostService {

    TeamPostDetailResponse createTeamPost(Long memberId, Long teamId, TeamPostCreateRequest request);

    List<TeamPostSummaryResponse> findTeamPosts(Long memberId, Long teamId);

    TeamPostDetailResponse findTeamPost(Long memberId, Long teamId, Long postId);

    TeamPostDetailResponse updateTeamPost(Long memberId, Long teamId, Long postId, TeamPostUpdateRequest request);

    void deleteTeamPost(Long memberId, Long teamId, Long postId);

}
