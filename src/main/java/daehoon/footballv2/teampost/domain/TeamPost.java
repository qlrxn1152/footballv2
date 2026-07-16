package daehoon.footballv2.teampost.domain;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.team.domain.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamPost {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_member_id", nullable = false)
    private Member authorMember;

    @Column(nullable = false, length =  100)
    private String title;

    @Column(nullable = false, length =  5000)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TeamPost(Team team, Member authorMember, String title, String content) {
        this.team = team;
        this.authorMember = authorMember;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateTeamPost(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }


}
