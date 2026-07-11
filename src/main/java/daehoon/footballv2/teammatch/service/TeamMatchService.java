package daehoon.footballv2.teammatch.service;

import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.response.TeamMatchAcceptResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchCreateResponse;
import daehoon.footballv2.teammatch.dto.response.TeamMatchSummaryResponse;

import java.util.List;

public interface TeamMatchService {

    TeamMatchCreateResponse createTeamMatch(Long teamId, Long memberId);

    TeamMatchAcceptResponse acceptTeamMatch(Long teamMatchId, Long awayLeaderMemberId);

    List<TeamMatchSummaryResponse> findTeamMatches(TeamMatchStatus status);

    List<TeamMatchSummaryResponse> findTeamMatches();


}
