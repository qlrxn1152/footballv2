package daehoon.footballv2.teammatch.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 100)
    private String stadiumName;

    @NotBlank
    @Size(max = 255)
    private String stadiumAddress;

    public TeamMatchCreateRequest(LocalDateTime playedAt, String stadiumName, String stadiumAddress) {
        this.playedAt = playedAt;
        this.stadiumName = stadiumName;
        this.stadiumAddress = stadiumAddress;
    }
}
