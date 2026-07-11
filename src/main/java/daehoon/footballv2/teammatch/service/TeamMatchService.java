package daehoon.footballv2.teammatch.service;

import daehoon.footballv2.teammatch.dto.response.TeamMatchCreateResponse;

public interface TeamMatchService {

    TeamMatchCreateResponse createTeamMatch(Long teamId, Long memberId);
}
