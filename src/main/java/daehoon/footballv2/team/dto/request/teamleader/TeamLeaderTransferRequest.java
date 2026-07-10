package daehoon.footballv2.team.dto.request.teamleader;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamLeaderTransferRequest {

    private Long newLeaderMemberId;
}
