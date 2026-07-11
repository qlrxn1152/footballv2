package daehoon.footballv2.teammatch.dto.response;

import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchResultResponse {

    private Long teamMatchId;

    private Long homeTeamId;
    private String homeTeamName;
    private Integer homeScore;

    private Long awayTeamId;
    private String awayTeamName;
    private Integer awayScore;

    private Long winnerTeamId;
    private String winnerTeamName;

    private TeamMatchStatus status;


    public TeamMatchResultResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeScore, Long awayTeamId, String awayTeamName, Integer awayScore, TeamMatchStatus status) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeScore = homeScore;
        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.awayScore = awayScore;
        this.status = status;

        // 무승부
        if (homeScore.equals(awayScore)) {
            this.winnerTeamId = null;
            this.winnerTeamName = null;
        }

        // 홈팀 승
        if (homeScore > awayScore) {
            this.winnerTeamId = homeTeamId;
            this.winnerTeamName = homeTeamName;
        }

        // 어웨이팀 승
        if (homeScore < awayScore) {
            this.winnerTeamId = awayTeamId;
            this.winnerTeamName = awayTeamName;
        }


    }
}
