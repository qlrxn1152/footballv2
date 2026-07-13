package daehoon.footballv2.teammatch.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchGoalCreateRequest {

    // 득점자에 대한 정보
    @NotNull
    private Long teamId; // 어떤팀에 속한사람임?

    @NotNull
    private Long scorerMemberId; // 그 팀에 누군데 ?

    @NotNull
    @Min(value = 1)
    private Integer goalCount; // 걔가 몇골넣었는데?

    public TeamMatchGoalCreateRequest(Long teamId, Long scorerMemberId, Integer goalCount) {
        this.teamId = teamId;
        this.scorerMemberId = scorerMemberId;
        this.goalCount = goalCount;
    }

}
