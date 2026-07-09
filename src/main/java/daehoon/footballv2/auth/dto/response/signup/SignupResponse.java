package daehoon.footballv2.auth.dto.response.signup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupResponse {

    private Long memberId;
    private String username;
    private int memberRating;

    public SignupResponse(Long memberId, String username, int memberRating) {
        this.memberId = memberId;
        this.username = username;
        this.memberRating = memberRating;
    }
}
