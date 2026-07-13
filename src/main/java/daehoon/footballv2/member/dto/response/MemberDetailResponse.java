package daehoon.footballv2.member.dto.response;

import daehoon.footballv2.team.domain.TeamRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class MemberDetailResponse {

    private Long memberId;
    private String username;
    private int memberRating;
    private Integer totalGoalCount;
    private Long teamId;
    private String teamName;
    private TeamRole teamRole;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;

    // 팀이 있는경우
    public MemberDetailResponse(Long memberId, String username, int memberRating, Integer totalGoalCount, Long teamId, String teamName, TeamRole teamRole, LocalDateTime joinedAt, LocalDateTime createdAt) {
        this.memberId = memberId;
        this.username = username;
        this.memberRating = memberRating;
        this.totalGoalCount = totalGoalCount;
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamRole = teamRole;
        this.joinedAt = joinedAt;
        this.createdAt = createdAt;
    }

    // 팀이 없는경우
    public MemberDetailResponse(Long memberId, String username, int memberRating, Integer totalGoalCount, LocalDateTime createdAt) {
        this.memberId = memberId;
        this.username = username;
        this.memberRating = memberRating;
        this.totalGoalCount = totalGoalCount;
        this.createdAt = createdAt;

        this.teamId = null;
        this.teamName = null;
        this.teamRole = null;
        this.joinedAt = null;
    }
}
