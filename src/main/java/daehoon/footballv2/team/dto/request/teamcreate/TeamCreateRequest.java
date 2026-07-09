package daehoon.footballv2.team.dto.request.teamcreate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamCreateRequest {

    @NotBlank
    @Size(min = 4, max = 20, message = "팀 이름은 4글자이상 20글자 사이여야합니다.")
    private String teamName;

    public TeamCreateRequest(String teamName) {
        this.teamName = teamName;
    }
}
