package daehoon.footballv2.teammatch.dto.response;

import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @NoArgsConstructor
 @Setter
public class TeamMatchSummaryResponse {

    private Long teamMatchId;

    private Long homeTeamId;
    private String homeTeamName;
    private Integer homeTeamRating;
    private Integer homeScore;

    private Long awayTeamId;
    private String awayTeamName;
    private Integer awayTeamRating;
    private Integer awayScore;

    private Long winnerTeamId;
    private String winnerTeamName;

    private TeamMatchStatus status;
    private LocalDateTime createdAt;

    // private LocalDateTime matchedAt; 나중에는, matchedAt 을 만들어서, 매치가 언제인지도 확인할수있게 ..

    // PENDING
    public TeamMatchSummaryResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating, TeamMatchStatus status, LocalDateTime createdAt) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.status = status;
        this.createdAt = createdAt;

        this.homeScore = null;
        this.awayScore = null;
        this.winnerTeamId = null;
        this.winnerTeamName = null;
    }

    // MATCHED, COMPLETED
    public TeamMatchSummaryResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating, Long awayTeamId, String awayTeamName, Integer awayTeamRating, TeamMatchStatus status, LocalDateTime createdAt) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.awayTeamRating = awayTeamRating;
        this.status = status;
        this.createdAt = createdAt;

        this.homeScore = null;
        this.awayScore = null;
        this.winnerTeamId = null;
        this.winnerTeamName = null;
    }

    // COMPLETED
    public TeamMatchSummaryResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating, Integer homeScore,
                                    Long awayTeamId, String awayTeamName, Integer awayTeamRating, Integer awayScore,
                                    Long winnerTeamId, String winnerTeamName, TeamMatchStatus status, LocalDateTime createdAt) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.homeScore = homeScore;

        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.awayTeamRating = awayTeamRating;
        this.awayScore = awayScore;

        this.winnerTeamId = winnerTeamId;
        this.winnerTeamName = winnerTeamName;

        this.status = status;
        this.createdAt = createdAt;
    }
}
