package daehoon.footballv2.teammatch.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TeamMatchCreateRequest {

    @NotNull(message = "경기 일시는 필수입니다.")
    @Future(message = "경기 일시는 현재 시간 이후여야 합니다.")
    private LocalDateTime playedAt;

    public TeamMatchCreateRequest(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}
