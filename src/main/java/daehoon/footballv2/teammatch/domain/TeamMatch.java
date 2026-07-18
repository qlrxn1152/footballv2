package daehoon.footballv2.teammatch.domain;

import daehoon.footballv2.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMatch {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_match_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id") // 우선 Null 허용 -> 매치등록하면, awayTeam = null.
    private Team awayTeam;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_match_status", nullable = false)
    private TeamMatchStatus status; // PENDING, MATCHED, COMPLETED

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt; // 매치날짜

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 매치요청 생성날짜가 언제인지

    @Column(name = "stadium_name", length = 100, nullable = false)
    private String stadiumName;

    @Column(name = "stadium_address", length = 255, nullable = false)
    private String stadiumAddress;

    // 매치생성
    public TeamMatch(Team homeTeam, LocalDateTime playedAt, String stadiumName, String stadiumAddress) {
        this.homeTeam = homeTeam;
        this.playedAt = playedAt; // 매치 진행날짜

        this.stadiumName = stadiumName;
        this.stadiumAddress = stadiumAddress;

        this.awayTeam = null;
        this.status = TeamMatchStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void matchedTheMatch(Team awayTeam) {
        this.awayTeam = awayTeam;
        this.status = TeamMatchStatus.MATCHED;
    }

    public void completedMatch(Integer homeScore, Integer awayScore) {
        this.status = TeamMatchStatus.COMPLETED;
        // 점수 반영 ...

        if (homeScore.equals(awayScore)) {
            this.homeTeam.draw();
            this.awayTeam.draw();
        }

        if (homeScore > awayScore) {
            this.homeTeam.win();
            this.awayTeam.lose();
        }

        if (homeScore < awayScore) {
            this.homeTeam.lose();
            this.awayTeam.win();
        }

    }

}
