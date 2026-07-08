package daehoon.footballv2.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LoginResponse {

    private Long memberId;
    private String username;
    private int memberRating;

    public LoginResponse(Long memberId, String username, int memberRating) {
        this.memberId = memberId;
        this.username = username;
        this.memberRating = memberRating;
    }
}
