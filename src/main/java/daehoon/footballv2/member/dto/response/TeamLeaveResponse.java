package daehoon.footballv2.member.dto.response;

import daehoon.footballv2.team.domain.TeamRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamLeaveResponse {

    private Long memberId;
    private String username;
    private Long teamId;
    private String teamName;
    private TeamRole teamRole;
    private boolean left;

    public TeamLeaveResponse(Long memberId, String username, Long teamId, String teamName, TeamRole teamRole) {
        this.memberId = memberId;
        this.username = username;
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamRole = teamRole;
        this.left = true;
    }
}
