package daehoon.footballv2.teammatch.dto.response;

import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchHistoryResponse {

    private Long teamMatchId;

    private Long homeTeamId;
    private String homeTeamName;

    private Long awayTeamId;
    private String awayTeamName;

    private TeamMatchStatus status;
    private LocalDateTime createdAt;

    private Integer homeScore;
    private Integer awayScore;

    private Long winnerTeamId;
    private String winnerTeamName;

    // PENDING -> home팀에대한 정보만 있음
    public TeamMatchHistoryResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, TeamMatchStatus status, LocalDateTime createdAt) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.status = status;
        this.createdAt = createdAt;

        this.awayTeamId = null;
        this.awayTeamName = null;
        this.homeScore = null;
        this.awayScore = null;
        this.winnerTeamId = null;
        this.winnerTeamName = null;
    }

    // MATCHED -> away팀에대한 정보도 있지만, 결과는 없음.
    public TeamMatchHistoryResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Long awayTeamId, String awayTeamName, TeamMatchStatus status, LocalDateTime createdAt) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.status = status;
        this.createdAt = createdAt;

        this.homeScore = null;
        this.awayScore = null;
        this.winnerTeamId = null;
        this.winnerTeamName = null;
    }

    // COMPLETED -> 매치결과와, 승리팀에 대한 정보도 있음.

    public TeamMatchHistoryResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Long awayTeamId, String awayTeamName, TeamMatchStatus status, LocalDateTime createdAt, Integer homeScore, Integer awayScore, Long winnerTeamId, String winnerTeamName) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;

        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;

        this.status = status;
        this.createdAt = createdAt;

        this.homeScore = homeScore;
        this.awayScore = awayScore;

        this.winnerTeamId = winnerTeamId;
        this.winnerTeamName = winnerTeamName;
    }
}
