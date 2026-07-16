package daehoon.footballv2.teampostcommnet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamPostCommentUpdateRequest {

    @NotBlank @Size(max = 1000)
    private String content;

    public TeamPostCommentUpdateRequest(String content) {
        this.content = content;
    }
}
