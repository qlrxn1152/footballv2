package daehoon.footballv2.team.dto.response.teamdisband;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamDisbandResponse {

    private Long teamId;
    private String teamName;
    private Long leaderMemberId;
    private String leaderUsername;
    private boolean disbanded;

    public TeamDisbandResponse(Long teamId, String teamName, Long leaderMemberId, String leaderUsername, boolean disbanded) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.leaderMemberId = leaderMemberId;
        this.leaderUsername = leaderUsername;
        this.disbanded = disbanded;
    }
}
