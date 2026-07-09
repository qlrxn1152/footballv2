package daehoon.footballv2.team.service;

import daehoon.footballv2.team.domain.TeamJoinRequest;
import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestCreateResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestDecisionResponse;
import daehoon.footballv2.team.dto.response.teamjoinrequest.TeamJoinRequestSummaryResponse;

import java.util.List;

public interface TeamService {

    TeamCreateResponse createTeam(String teamName, Long memberId);

    TeamJoinRequestCreateResponse joinRequest(Long teamId, Long memberId);

    TeamJoinRequestDecisionResponse acceptRequest(Long joinRequestId, Long teamId, Long memberId);

    TeamJoinRequestDecisionResponse rejectRequest(Long joinRequestId, Long teamId, Long memberId);

    List<TeamJoinRequestSummaryResponse> findJoinRequests(Long teamId, Long leaderMemberId, TeamJoinRequestStatus status);

}
