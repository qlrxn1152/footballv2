package daehoon.footballv2.teammatch.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchResultCreateRequest {

    @NotBlank
    @Min(value = 0)
    private int homeScore;

    @NotBlank
    @Min(value = 0)
    private int awayScore;

    public TeamMatchResultCreateRequest(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }
}
