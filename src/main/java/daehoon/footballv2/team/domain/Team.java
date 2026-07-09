package daehoon.footballv2.team.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    private static final int DEFAULT_TEAM_RATING = 1500;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_name", nullable = false, unique = true)
    private String teamName;

    @Column(name = "team_rating", nullable = false)
    private int teamRating;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    public Team(String teamName) {
        this.teamName = teamName;
        this.teamRating = DEFAULT_TEAM_RATING;
        this.createdAt = LocalDate.now();
    }

}
