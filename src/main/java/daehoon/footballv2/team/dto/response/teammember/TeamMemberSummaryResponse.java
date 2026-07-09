package daehoon.footballv2.team.dto.response.teammember;


import daehoon.footballv2.team.domain.TeamRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class TeamMemberSummaryResponse {

    private Long teamMemberId;
    private Long teamId;
    private String teamName;
    private Long memberId;
    private String username;
    private int memberRating;
    private TeamRole teamRole;
    private LocalDateTime joinedAt;

    public TeamMemberSummaryResponse(Long teamMemberId, Long teamId, String teamName, Long memberId, String username, int memberRating, TeamRole teamRole, LocalDateTime joinedAt) {
        this.teamMemberId = teamMemberId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.memberId = memberId;
        this.username = username;
        this.memberRating = memberRating;
        this.teamRole = teamRole;
        this.joinedAt = joinedAt;
    }
}
