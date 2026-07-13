package daehoon.footballv2.teammatch.domain;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.team.domain.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMatchGoal {

    // 이 도메인이 만들어졌다는건, 1개의 매치의 한 선수 득점기록 1개임 ( 득점자가 2명이면, 이 객체는 2개 ) 그냥, 해당매치에 특정선수의 득점기록을 확인할수있는 객체


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_match_goal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_match_id", nullable = false)
    private TeamMatch teamMatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scorer_member_id")
    private Member scorerMember;

    @NotNull
    @Min(value = 1)
    private Integer goalCount;

    private LocalDateTime createdAt;

    public TeamMatchGoal(TeamMatch teamMatch, Team team, Member scorerMember, Integer goalCount) {
        this.teamMatch = teamMatch;
        this.team = team;
        this.scorerMember = scorerMember;
        this.goalCount = goalCount;
        this.createdAt = LocalDateTime.now();
    }

}
