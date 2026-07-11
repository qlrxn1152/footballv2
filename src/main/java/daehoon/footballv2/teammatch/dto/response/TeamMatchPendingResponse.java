package daehoon.footballv2.teammatch.dto.response;

import daehoon.footballv2.teammatch.domain.TeamMatchStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchPendingResponse {

    private Long teamMatchId;
    private Long homeTeamId;
    private String homeTeamName;
    private Integer homeTeamRating;
    private TeamMatchStatus status;
    private LocalDateTime createdAt; // 매치 요청이 언제 만들어졌는지.
    // 나중에는, matchedAt 을 만들어서, 매치가 언제인지도 확인할수있게 ..

    public TeamMatchPendingResponse(Long teamMatchId, Long homeTeamId, String homeTeamName, Integer homeTeamRating, TeamMatchStatus status, LocalDateTime createdAt) {
        this.teamMatchId = teamMatchId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeTeamName;
        this.homeTeamRating = homeTeamRating;
        this.status = status;
        this.createdAt = createdAt;
    }
}
