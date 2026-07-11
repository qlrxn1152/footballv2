package daehoon.footballv2.teammatch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMatch {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_match_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_match_status", nullable = false)
    private TeamMatchStatus status;
}
