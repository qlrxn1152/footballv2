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

    private Long awayTeamId;
    private String awayTeamName;
    private Integer awayTeamRating;

    private TeamMatchStatus status;
    private LocalDateTime createdAt;

    // PENDING
    public TeamMatchSummaryResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating, TeamMatchStatus status, LocalDateTime createdAt) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.status = status;
        this.createdAt = createdAt;
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
    }
}
