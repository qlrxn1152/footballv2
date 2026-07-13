package daehoon.footballv2.teammatch.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchGoalResponse {

    private Long teamId;
    private Long teamMatchId;
    private Long scorerMemberId;
    private String scorerUsername;
    private Integer goalCount;

    public TeamMatchGoalResponse(Long teamId, Long teamMatchId, Long scorerMemberId, String scorerUsername, Integer goalCount) {
        this.teamId = teamId;
        this.teamMatchId = teamMatchId;
        this.scorerMemberId = scorerMemberId;
        this.scorerUsername = scorerUsername;
        this.goalCount = goalCount;
    }
}
