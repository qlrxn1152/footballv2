package daehoon.footballv2.teammatch.service;

import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import daehoon.footballv2.teammatch.dto.response.*;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamMatchService {

    TeamMatchCreateResponse createTeamMatch(Long teamId, Long memberId, LocalDateTime playedAt);

    TeamMatchAcceptResponse acceptTeamMatch(Long teamMatchId, Long awayLeaderMemberId);

    List<TeamMatchSummaryResponse> findTeamMatches(TeamMatchStatus status); // 모든팀들 status 에 따라서 ..

    List<TeamMatchSummaryResponse> findTeamMatches(); // 모든팀들 조회

    TeamMatchResultResponse registerMatchResult(Long teamMatchId, Long homeLeaderMemberId, Integer homeScore, Integer awayScore);

    List<TeamMatchHistoryResponse> findTeamMatchHistory(Long teamId, TeamMatchStatus status);

    TeamMatchDetailResponse findTeamMatchDetail(Long teamMatchId);



}
