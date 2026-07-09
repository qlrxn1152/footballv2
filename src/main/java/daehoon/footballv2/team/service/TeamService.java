package daehoon.footballv2.team.service;

import daehoon.footballv2.team.dto.response.teamcreate.TeamCreateResponse;

public interface TeamService {

    TeamCreateResponse createTeam(String teamName, Long memberId);
}
