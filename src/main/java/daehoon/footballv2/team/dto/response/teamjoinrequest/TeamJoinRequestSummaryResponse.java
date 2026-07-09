package daehoon.footballv2.team.dto.response.teamjoinrequest;

import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamJoinRequestSummaryResponse {

    private Long teamJoinRequestId;
    private Long teamId;
    private String teamName;

    private Long memberId;
    private String username;

    private TeamJoinRequestStatus status;

    private LocalDateTime createdAt;

    public TeamJoinRequestSummaryResponse(Long teamJoinRequestId, Long teamId, String teamName, Long memberId, String username, TeamJoinRequestStatus status, LocalDateTime createdAt) {
        this.teamJoinRequestId = teamJoinRequestId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.memberId = memberId;
        this.username = username;
        this.status = status;
        this.createdAt = createdAt;
    }
}
