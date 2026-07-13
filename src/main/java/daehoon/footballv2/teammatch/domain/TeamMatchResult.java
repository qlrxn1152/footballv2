package daehoon.footballv2.teammatch.domain;

import daehoon.footballv2.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMatchResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_match_result_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_match_id", nullable = false, unique = true)
    private TeamMatch teamMatch;

    @Column(name = "home_score", nullable = false)
    private Integer homeScore;

    @Column(name = "away_score", nullable = false)
    private Integer awayScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @Column(name = "created_at")
    private LocalDateTime createdAt;




    public TeamMatchResult(TeamMatch teamMatch, Integer homeScore, Integer awayScore) {
        this.teamMatch = teamMatch;
        this.homeScore = homeScore;
        this.awayScore = awayScore;

        // 무승부시, 양팀 점수 + 10
        if (homeScore.equals(awayScore)) {
            this.winnerTeam = null; // null 이 맞을까? // Optional?
        }

        // 승리팀 점수 + 30 , 패배팀 점수 - 30
        if (homeScore > awayScore) {
            this.winnerTeam = teamMatch.getHomeTeam();
        }

        if ( awayScore > homeScore) {
            this.winnerTeam = teamMatch.getAwayTeam();
        }


        this.createdAt = LocalDateTime.now();
    }
}
