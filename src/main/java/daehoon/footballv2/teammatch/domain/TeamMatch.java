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

    /*
    @Column(name = "matched_at", nullable = false)
    private LocalDateTime matchedAt; // 매치날짜, 시간이 언제인지. -> 요청을 등록할때, 날짜를 선택할수있게해서, 매치날짜를 선택할수있게 정해줘야함 ...
     */

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 매치요청 생성날짜가 언제인지

    public TeamMatch(Team homeTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = null;
        this.status = TeamMatchStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void matchedTheMatch(Team awayTeam) {
        this.awayTeam = awayTeam;
        this.status = TeamMatchStatus.MATCHED;
    }

}
