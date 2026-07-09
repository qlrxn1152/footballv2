package daehoon.footballv2.team.dto.response.teamdetail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamDetailResponse {

    private Long teamId;
    private String teamName;
    private int teamRating;
    private Long leaderMemberId;
    private String leaderUsername;
    private int memberCount;
    private LocalDateTime createdAt;

    public TeamDetailResponse(Long teamId, String teamName, int teamRating, Long leaderMemberId, String leaderUsername, int memberCount, LocalDateTime createdAt) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamRating = teamRating;
        this.leaderMemberId = leaderMemberId;
        this.leaderUsername = leaderUsername;
        this.memberCount = memberCount;
        this.createdAt = createdAt;
    }
}
