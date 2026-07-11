package daehoon.footballv2.teammatch.dto.response;

import daehoon.footballv2.team.domain.Team;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchCreateResponse {

    private Long teamMatchId;
    private Long homeTeamId;
    private String homeTeamName;
    private Integer homeTeamRating;
    private TeamMatchStatus status;

    // 팀 매치를 생성하는 시점 -> awayTeam => null
    public TeamMatchCreateResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating, TeamMatchStatus status) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.status = status;
    }


}
