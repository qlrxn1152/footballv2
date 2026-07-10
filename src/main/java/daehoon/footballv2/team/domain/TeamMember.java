package daehoon.footballv2.team.domain;

import daehoon.footballv2.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_role", nullable = false)
    private TeamRole teamRole;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    public TeamMember(Team team, Member member, TeamRole teamRole) {
        this.team = team;
        this.member = member;
        this.teamRole = teamRole;
        this.joinedAt = LocalDateTime.now();
    }

    public void leaderToMember() {
        this.teamRole = TeamRole.MEMBER;
    }

    public void memberToLeader() {
        this.teamRole = TeamRole.LEADER;
    }

}
