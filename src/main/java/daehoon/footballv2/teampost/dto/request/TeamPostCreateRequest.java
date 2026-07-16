package daehoon.footballv2.teampost.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TeamPostCreateRequest {

    @NotBlank @Max(100)
    private String title;

    @NotBlank @Max(5000)
    private String content;

    public TeamPostCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
