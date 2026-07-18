package daehoon.footballv2.teammatch.dto.response;

import daehoon.footballv2.team.domain.Team;
import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchCreateResponse {

    private Long teamMatchId;
    private Long homeTeamId;
    private String homeTeamName;
    private Integer homeTeamRating;
    private TeamMatchStatus status;

    private String stadiumName;
    private String stadiumAddress;

    private LocalDateTime playedAt;
    private LocalDateTime createdAt;

    // 팀 매치를 생성하는 시점 -> awayTeam => null
    public TeamMatchCreateResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating, TeamMatchStatus status, String stadiumName, String stadiumAddress, LocalDateTime playedAt, LocalDateTime createdAt) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.status = status;
        this.stadiumName = stadiumName;
        this.stadiumAddress = stadiumAddress;
        this.playedAt = playedAt;
        this.createdAt = LocalDateTime.now();
    }
}
