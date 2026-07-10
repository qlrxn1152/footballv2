package daehoon.footballv2.team.dto.response.teamleader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamLeaderTransferResponse {

    private Long teamId;
    private String teamName;
    private Long oldLeaderMemberId;
    private String oldLeaderUsername;

    private Long newLeaderMemberId;
    private String newLeaderUsername;

    public TeamLeaderTransferResponse(Long teamId, String teamName, Long oldLeaderMemberId, String oldLeaderUsername, Long newLeaderMemberId, String newLeaderUsername) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.oldLeaderMemberId = oldLeaderMemberId;
        this.oldLeaderUsername = oldLeaderUsername;
        this.newLeaderMemberId = newLeaderMemberId;
        this.newLeaderUsername = newLeaderUsername;
    }
}
