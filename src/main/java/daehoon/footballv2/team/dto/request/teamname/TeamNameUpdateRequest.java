package daehoon.footballv2.team.dto.request.teamname;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamNameUpdateRequest {

    private String teamName;

    public TeamNameUpdateRequest(String teamName) {
        this.teamName = teamName;
    }
}
