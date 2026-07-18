package daehoon.footballv2.teammatch.dto.response;

import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchDetailResponse {

    private Long teamMatchId;

    private Long homeTeamId;
    private String homeTeamName;
    private Integer homeTeamRating;

    private Long awayTeamId;
    private String awayTeamName;
    private Integer awayTeamRating;

    private String stadiumName;
    private String stadiumAddress;

    private TeamMatchStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime playedAt;



    // == COMPLETED 인 경우에만 가집니다. 나머지는 Null
    private Integer homeScore;
    private Integer awayScore;

    private Long winnerTeamId;
    private String winnerTeamName;


    // PENDING 인 경우
    public TeamMatchDetailResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating, TeamMatchStatus status, LocalDateTime createdAt, LocalDateTime playedAt, String stadiumName, String stadiumAddress) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.status = status;
        this.createdAt = createdAt;
        this.playedAt = playedAt;

        this.stadiumName = stadiumName;
        this.stadiumAddress = stadiumAddress;

        this.awayTeamId = null;
        this.awayTeamName = null;
        this.awayTeamRating = null;

        this.homeScore = null;
        this.awayScore = null;
        this.winnerTeamId = null;
        this.winnerTeamName = null;
    }

    //MATCHED 인 경우
    public TeamMatchDetailResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating,
                                   Long awayTeamId, String awayTeamName, Integer awayTeamRating,
                                   TeamMatchStatus status, LocalDateTime createdAt, LocalDateTime playedAt,
                                   String stadiumName, String stadiumAddress) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.stadiumName = stadiumName;
        this.stadiumAddress = stadiumAddress;

        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.awayTeamRating = awayTeamRating;

        this.status = status;
        this.createdAt = createdAt;
        this.playedAt = playedAt;

        this.homeScore = null;
        this.awayScore = null;
        this.winnerTeamId = null;
        this.winnerTeamName = null;
    }

    // COMPLETED 인 경우

    public TeamMatchDetailResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating,
                                   Long awayTeamId, String awayTeamName, Integer awayTeamRating,
                                   TeamMatchStatus status, LocalDateTime createdAt, LocalDateTime playedAt,
                                   Integer homeScore, Integer awayScore, Long winnerTeamId, String winnerTeamName,
                                   String stadiumName, String stadiumAddress) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;

        this.stadiumName = stadiumName;
        this.stadiumAddress = stadiumAddress;

        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayTeamName;
        this.awayTeamRating = awayTeamRating;

        this.status = status;
        this.createdAt = createdAt;
        this.playedAt = playedAt;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.winnerTeamId = winnerTeamId;
        this.winnerTeamName = winnerTeamName;
    }
}
