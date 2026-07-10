package daehoon.footballv2.team.domain;

import daehoon.footballv2.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamJoinRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_join_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "team_join_request_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamJoinRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TeamJoinRequest(Team team, Member member) {
        this.team = team;
        this.member = member;
        this.status = TeamJoinRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void acceptedRequest() {
        this.status = TeamJoinRequestStatus.ACCEPTED;
    }

    public void rejectedRequest() {
        this.status = TeamJoinRequestStatus.REJECTED;
    }

    public void canceledRequest() {
        this.status = TeamJoinRequestStatus.CANCELED;
    }


}
