package daehoon.footballv2.team.dto.response.teamcreate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamCreateResponse {

    private Long teamId;
    private String teamName;
    private int teamRating;
    private Long leaderMemberId;
    private String leaderUsername;

    public TeamCreateResponse(Long teamId, String teamName, int teamRating, Long leaderMemberId, String leaderUsername) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamRating = teamRating;
        this.leaderMemberId = leaderMemberId;
        this.leaderUsername = leaderUsername;
    }
}
