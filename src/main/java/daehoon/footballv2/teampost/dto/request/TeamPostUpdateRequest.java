package daehoon.footballv2.teampost.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamPostUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank @Size(max = 5000)
    private String content;

    public TeamPostUpdateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
