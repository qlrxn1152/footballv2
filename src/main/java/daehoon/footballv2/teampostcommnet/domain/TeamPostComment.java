package daehoon.footballv2.teampostcommnet.domain;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.teampost.domain.TeamPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamPostComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_post_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_post_id", nullable = false)
    private TeamPost teamPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authoer_member_id", nullable = false)
    private Member authorMember;

    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TeamPostComment(TeamPost teamPost, Member authorMember, String content) {
        this.teamPost = teamPost;
        this.authorMember = authorMember;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateComment(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }


}
