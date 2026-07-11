package daehoon.footballv2.teammatch.service;

import daehoon.footballv2.teammatch.dto.response.TeamMatchCreateResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchPendingResponse;

import java.util.List;

public interface TeamMatchService {

    TeamMatchCreateResponse createTeamMatch(Long teamId, Long memberId);

    List<TeamMatchPendingResponse> findPendingTeamMatches();
}
