package daehoon.footballv2.teammatch.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchCreateRequest {

    @NotNull
    private LocalDateTime playedAt;

    public TeamMatchCreateRequest(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}
