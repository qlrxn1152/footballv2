package daehoon.footballv2.team.dto.response.teamjoinrequest;

import daehoon.footballv2.team.domain.TeamJoinRequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamJoinRequestDecisionResponse {

    private Long teamJoinRequestId;
    private Long teamId;
    private String teamName;

    private Long memberId;
    private String username;

    private TeamJoinRequestStatus status;

    public TeamJoinRequestDecisionResponse(Long teamJoinRequestId, Long teamId, String teamName, Long memberId, String username, TeamJoinRequestStatus status) {
        this.teamJoinRequestId = teamJoinRequestId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.memberId = memberId;
        this.username = username;
        this.status = status;
    }
}
