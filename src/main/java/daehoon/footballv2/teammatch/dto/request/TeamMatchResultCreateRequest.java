package daehoon.footballv2.teammatch.dto.request;

import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.teammatch.domain.TeamMatchGoal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchResultCreateRequest {

    @NotNull
    @Min(value = 0)
    private Integer homeScore;

    @NotNull
    @Min(value = 0)
    private Integer awayScore;

    // 득점자 구현
    @NotNull
    @Valid
    private List<TeamMatchGoalCreateRequest> goals = new ArrayList<>();

    // 해당 매치에 어떤팀의 누가 몇골넣었는지 확인가능.


    // 양팀 다 득점하지못함 -> 득점자가 없음 ==> 빈 배열
    public TeamMatchResultCreateRequest(Integer homeScore, Integer awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }




}
